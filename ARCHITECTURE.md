# League of Legends Esports Platform — Architecture

A multi-service, AWS-native platform that scrapes League of Legends esports data from Leaguepedia, stores it in DynamoDB, and exposes it via a public REST/GraphQL/gRPC API. A Discord prediction bot consumes that API and lets users make match predictions.

---

## System Overview

```
┌──────────────────────────────────────────────────────────────┐
│                         DATA SOURCES                        │
│               https://lol.fandom.com (Leaguepedia)          │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
                     ┌────────────────────────┐
                     │  leaguepedia-loader     │
                     │  Python · CLI · Docker  │
                     │  Scraper + ETL          │
                     └────────────┬───────────┘
                                  │  boto3 writes
                                  ▼
              ┌────────────────────────────────────────┐
              │        AWS DynamoDB (us-west-2)        │
              │  Leagues · Tournaments · Matches ·     │
              │  Players · Teams                       │
              └──────────────┬─────────────────────────┘
                             │  read-only
                             ▼
         ┌────────────────────────┐ ┌────────────────────────┐
         │    lol-api-service     │ │ lol-api-service-rust   │
         │  Kotlin / Spring Boot  │ │  Rust / Tonic (WIP)    │
         │  REST + gRPC + GraphQL │ │  gRPC only             │
         │  Port 8080 / 9090      │ │  Port 3000             │
         └────────────┬───────────┘ └────────────────────────┘
                      │  public endpoint
                      │  https://api.lol-esports.mckernant1.com
                      ▼
       ┌──────────────────────────────────────────────────────┐
       │              lol-openapi (spec + SDKs)               │
       │  defines the REST contract · generates clients:      │
       │  Java (esports-api lib) · Rust · TypeScript · GQL    │
       └──────────────────────┬───────────────────────────────┘
                              │  Java SDK (esports-api)
                              ▼
       ┌──────────────────────────────────────────────────────┐
       │              lol-predictions-bot                     │
       │  Kotlin / JDA 6 (Discord bot)                        │
       │  consumes the API via the generated esports-api SDK  │
       │  Slash commands · Predictions · Reminders            │
       │  DynamoDB: Predictions + UserSettings                │
       └──────────────────────┬───────────────────────────────┘
                              │
                              ▼
                         Discord Users
```

---

## Projects

### 1. `leaguepedia-loader` — Data Ingestion

| | |
|---|---|
| **Path** | `leaguepedia-loader/` |
| **Language** | Python 3.13 |
| **Package Manager** | uv (`pyproject.toml`, `uv.lock`) |
| **Output** | CLI binary via PyInstaller (`leaguepedia-loader`) |

**Purpose:** Scrapes Leaguepedia (lol.fandom.com) Cargo tables and writes items into DynamoDB tables: `Leagues`, `Tournaments`, `Matches`, `Players`, `Teams`.

**Key modules:**

| File | Role |
|---|---|
| `src/load_everything.py` | CLI entry point — subcommands: `leagues`, `tourneys`, `matches`, `players`, `teams` |
| `src/data_loading.py` | Orchestrates Cargo queries and DynamoDB `put_item` writes (skip-if-unchanged optimization) |
| `src/leaguepedia/leaguepedia.py` | `LeaguepediaSite` — authenticated `mwrogue.EsportsClient`, paginated Cargo queries in batches of 500 with 250ms throttle |
| `src/models/*.py` | Dataclasses mapping to DynamoDB item shapes: `League`, `Match`, `Player`, `Team`, `Tournament` |

**Data model (DynamoDB keys):**

| Table | PK | SK / GSI |
|---|---|---|
| `Leagues` | `leagueId` | — |
| `Tournaments` | `leagueId` | `tournamentId` |
| `Matches` | `tournamentId` | `matchId` |
| `Players` | `teamId` | `id` (GSI: `id-index`) |
| `Teams` | `teamId` | — |

**Auth:** `LEAGUEPEDIA_USERNAME` / `LEAGUEPEDIA_PASSWORD` env vars.

**Deployment:** Two-stage `Dockerfile` (uv build → PyInstaller bundle). Base images pulled from private ECR mirror (`653528873951.dkr.ecr.us-west-2.amazonaws.com`). Runs on a schedule (or manually) to keep DynamoDB fresh.

---

### 2. `lol-grpc-models` — Shared gRPC Contract

| | |
|---|---|
| **Path** | `lol-grpc-models/` |
| **Content** | Proto3 definitions only |

Includes five service definitions consumed by both `lol-api-service` (Kotlin) and `lol-api-service-rust` as a **git submodule**:

```
proto/com/mckernant1/lol/
├── league_service.proto      # ListLeagues, GetLeague
├── team_service.proto        # ListTeams, GetTeam
├── tournament_service.proto  # GetTournament, GetTournamentsForLeague,
│                             # GetOngoingTournaments, GetMostRecentTournament
├── match_service.proto       # GetMatchesForTournament, GetMatchesForLeague
└── player_service.proto      # GetPlayersOnTeam
```

> **Divergence warning:** The Kotlin and Rust projects pin different commits of this submodule. The Kotlin version includes `GetMatchesForLeague` and `google.protobuf.Timestamp` for `start_time`, while the Rust version is behind. Update both submodules in lockstep before building.

---

### 3. `lol-openapi` — API Specification & Generated SDKs

| | |
|---|---|
| **Path** | `lol-openapi/` |
| **Spec** | OpenAPI 3.0.2 (`root.yaml`) |
| **Server** | `https://api.lol-esports.mckernant1.com` |

**Purpose:** Contract-first OpenAPI spec that drives all public SDK generation and documentation. Not a runnable service itself.

**Spec structure:**

| Path | Content |
|---|---|
| `root.yaml` | Top-level spec — 11 path refs, 5 schema refs, `ApiKeyAuth` security |
| `paths/*.yaml` | 11 GET endpoint definitions |
| `components/schemas/ddb/models/*.yaml` | Covering schemas matching DynamoDB item shapes 1:1 |
| `components/schemas/errors/` | `EsportsServerError`, `NotFound`, `BadRequest`, `InternalServerError` |

**Generated outputs (committed, regenerated via OpenAPI Generator CLI 5.4.0):**

| Directory | Target | Publish |
|---|---|---|
| `gen/` | Java SDK (Gradle) | Maven (S3-backed) → `com.github.mckernant1.lol:esports-api` |
| `gen-rust/` | Rust crate (`lol-esports-api`) | crates.io |
| `gen-graphql/` | GraphQL schema | Consumed by `lol-api-service` |
| `docs/` | HTML documentation | GitHub Pages |

**CI/CD:** AWS CodeBuild buildspecs under `buildspecs/`:

| Buildspec | Secret Source | Destination |
|---|---|---|
| `publish-gradle.yaml` | AWS IAM | `s3://mvn.mckernant1.com/release` |
| `publish-crate.yaml` | SSM Parameter Store (`cratesio-publish`) | crates.io |
| `publish-npm.yaml` | SSM Parameter Store (`npm-token`) | npm (`@mckernant1/lol_esports_api`) |

---

### 4. `lol-api-service` — Primary Backend (Kotlin)

| | |
|---|---|
| **Path** | `lol-api-service/` |
| **Language** | Kotlin 2.3.10 (JVM 21) |
| **Framework** | Spring Boot 4.0.3 |
| **Ports** | 8080 (REST/GraphQL via nginx), 9090 (gRPC) |

**Purpose:** Serves League of Legends esports data from DynamoDB over three interfaces simultaneously.

**Interfaces:**

| Interface | Details |
|---|---|
| **REST** | 12 GET endpoints (`/leagues`, `/teams`, `/matches`, `/players`, `/ongoing-tournaments`, `/tournament/{id}`, `/tournaments/{leagueId}`, `/most-recent-tournament/{leagueId}`) |
| **GraphQL** | `/graphql` endpoint + `/graphiql` UI; 10 root queries with field resolvers for related entities |
| **gRPC** | 5 service implementations (`LeagueService`, `TeamService`, `TournamentService`, `MatchService`, `PlayerService`) — port 9090 |

**Key source structure:**

```
src/main/kotlin/com/mckernant1/lol/esports/api/
├── Runner.kt                     # @SpringBootApplication main
├── config/
│   ├── AwsConfig.kt              # DynamoDbClient bean
│   ├── Constants.kt              # Table names + GSI names
│   ├── GrpcExceptionHandler.kt  # StatusException → gRPC Status mapping
│   ├── MetricsConfig.kt          # CloudWatch metrics (namespace: Lol-Esports/Api)
│   └── SecurityConfig.kt         # HTTP route whitelist
├── graphql/GraphQLController.kt  # @SchemaMapping resolvers
├── grpc/*GrpcService.kt         # @GrpcService implementations (suspend functions)
├── rest/*Controller.kt           # @RestController GET endpoints
└── svc/*Service.kt               # DynamoDB read logic per entity
```

**DynamoDB tables** (same as leaguepedia-loader writes to):
`Leagues`, `Tournaments`, `Matches`, `Players`, `Teams` — with GSIs `id-index` (Players) and `tournamentId-index` (Tournaments).

**Caching:** Guava `LoadingCache` on league lookups (30 min access / 12 hr write expiry).

**Metrics:** CloudWatch namespace `Lol-Esports/Api`, daily cache-stats submission, request-level `X-Request-Id` logging.

**Deployment:**
- Multi-stage `Dockerfile`: builds with Temurin 21, runs under `supervisord` (nginx on 8000 → grpc_pass to 9090 + Java on 8080).
- GitHub Actions: `build_and_push.yaml` (push to `main` → ECR), `pr-check.yml` (Gradle build), `codeql.yaml`, `dependabot.yml`.

---

### 5. `lol-api-service-rust` — Rust Rewrite (WIP)

| | |
|---|---|
| **Path** | `lol-api-service-rust/` |
| **Language** | Rust 2021 edition |
| **Framework** | Tonic 0.12 (gRPC) + Tokio |
| **Status** | Early stage — gRPC only, no deployment config |

**Purpose:** A from-scratch Rust reimplementation of `lol-api-service`. Currently gRPC-only (no REST or GraphQL), with no Dockerfile or CI.

**Key source structure:**

```
src/
├── main.rs                       # tokio main → tonic Server on 127.0.0.1:3000
├── config/mod.rs                 # AWS config + DynamoDB client (default chain)
├── grpc.rs                       # Proto module wrapper (include_proto!)
├── result.rs                     # GwenError → tonic::Status conversion
├── services/*_service.rs         # 5 gRPC service implementations
├── data_access/*_access.rs       # DynamoDB read operations (serde_dynamo)
└── util/                         # DDB scan/query helpers, Timestamp conversion
```

**DynamoDB access:** Same 5 tables via `aws-sdk-dynamodb` + `serde_dynamo` (JSON-like `#[serde(rename_all="camelCase")]` mapping).

**Not yet implemented:** REST endpoints, GraphQL, caching, metrics, deployment, `GetMatchesForLeague` RPC (submodule too old).

---

### 6. `lol-predictions-bot` — Discord Bot

| | |
|---|---|
| **Path** | `lol-predictions-bot/` |
| **Language** | Kotlin 2.3.10 (JVM 21) |
| **Framework** | Discord JDA 6.3.2 |
| **Status** | **Taken offline** (retired per README) |

**Purpose:** A Discord bot providing esports info (schedules, results, standings, rosters) and a community prediction game. Users predict match winners via reactions; the bot scores predictions and reports stats.

**Slash commands:**

| Category | Commands |
|---|---|
| Esports | `/ongoing`, `/schedule`, `/results`, `/standings`, `/roster`, `/record` |
| Predictions | `/predict`, `/predictions`, `/report`, `/stats` |
| Settings | `/setTimezone`, `/setPasta`, `/pasta` |
| Reminders | `/addReminder`, `/listReminders`, `/deleteReminder` |

**Key source structure:**

```
src/main/kotlin/com/mckernant1/lol/blitzcrank/
├── Runner.kt                     # JDA builder, command registration
├── core/
│   ├── MessageListener.kt        # JDA ListenerAdapter — command dispatch
│   └── CommandLists.kt           # Command registry map
├── commands/                      # DiscordCommand implementations per slash command
├── model/
│   ├── Prediction.kt             # DynamoDB model (PK: discordId, SK: matchId)
│   └── UserSettings.kt           # DynamoDB model (PK: discordId, timezone, pasta, reminders)
├── timers/
│   ├── Reminders.kt              # Scans UserSettings, DMs users before matches
│   └── PublishMetricsTimer.kt    # CloudWatch metrics every 5 min
└── utils/
    ├── SingletonDeclarations.kt  # AWS clients, OkHttp cache, API client
    └── ApiFunctions.kt           # Esports API call wrappers
```

**Data storage:**

| Table | Key | Data |
|---|---|---|
| Predictions (env `PREDICTIONS_TABLE_NAME`) | PK: `discordId`, SK: `matchId` | Prediction string, GSI `games-by-match-id-index` |
| UserSettings (env `USER_SETTINGS_TABLE_NAME`) | PK: `discordId` | `timezone`, `pasta` (copy-paste text), `reminders` (JSON list) |

**API consumption:** Calls the esports API (`https://v2-api.lol-esports.mckernant1.com`) via the generated `com.mckernant1.lol:esports-api` Java client, with a 50 MiB OkHttp disk cache in `store/`.

**Required env vars:** `BOT_TOKEN`, `ESPORTS_API_KEY`, `PREDICTIONS_TABLE_NAME`, `USER_SETTINGS_TABLE_NAME`, `BOT_APPLICATION_ID`.

**Metrics:** CloudWatch namespace `Discord-bots/Predictions-Bot` (toggled by `METRICS_ENABLED` env var).

**Deployment:** Multi-stage Dockerfile (Eclipse Temurin 21 → Shadow JAR). GitHub Actions: `pr-check.yml`, `codeql.yaml`, `dependabot.yml`.

---

### 7. `lol-predictions-bot-models` — Legacy Model Library

| | |
|---|---|
| **Path** | `lol-predictions-bot-models/` |
| **Language** | Kotlin 1.7.0 (JVM 8) |
| **Status** | Legacy/superseded |

A standalone library that was published to the S3 Maven repo (`s3://mvn.mckernant1.com/release`) containing DynamoDB models + DAO classes for `Prediction` and `UserSettings`. The current `lol-predictions-bot` carries its own copies of these models inline and no longer depends on this artifact.

---

### 8. `credit-api` — Community Credit Score API (deleted)

A Spring Boot 3.5.5 REST API for a "League community credit score" system. It used:
- DynamoDB tables (`User`, `Report` entities) via `dynamodb-enhanced`
- Riot Games API integration (`RiotClient`) for in-game identity validation
- OAuth2 resource server + client (likely Discord JWT validation)
- Spring profiles (`application-dev.yaml`, `application-prod.yaml`)

This project was deleted from disk during this analysis and could not be fully recovered.

---

### 9. `lobby-variance` — Desktop Utility (deleted)

A React 19 + TypeScript + Vite frontend wrapped in a Tauri 2 desktop shell, using `seedrandom` for deterministic RNG. Presumably a client-side utility for analyzing lobby variance/randomness. This project was deleted from disk during this analysis.

---

## Infrastructure & Deployment

### AWS Account

- **Account ID:** `653528873951`
- **DynamoDB region:** `us-west-2`
- **ECR region:** `us-west-2`
- **CloudWatch dashboard:** `us-east-1`

### Docker Images

All Dockerfiles use private ECR base images (`653528873951.dkr.ecr.us-west-2.amazonaws.com/...`).

| Service | Base | Runtime |
|---|---|---|
| `lol-api-service` | Temurin 21 + nginx + supervisord | Java 21 JRE + nginx |
| `lol-predictions-bot` | Eclipse Temurin 21 | Java 21 JRE |
| `leaguepedia-loader` | Python 3.13-slim + uv + PyInstaller | Standalone binary |

### CI/CD (GitHub Actions)

| Repo | Workflows |
|---|---|
| `lol-api-service` | `build_and_push.yaml` (ECR), `pr-check.yml`, `codeql.yaml`, `dependabot.yml` |
| `lol-predictions-bot` | `pr-check.yml`, `codeql.yaml`, `dependabot.yml` |
| `leaguepedia-loader` | `pr-check.yml`, `dependabot.yml` |

### SDK Publishing (AWS CodeBuild)

The `lol-openapi` repo publishes generated client SDKs via CodeBuild buildspecs:

| Target | Secret (SSM Parameter Store) | Registry |
|---|---|---|
| Java | AWS IAM | `s3://mvn.mckernant1.com/release` |
| Rust | `cratesio-publish` | crates.io |
| TypeScript | `npm-token` | npm (`@mckernant1/lol_esports_api`) |

---

## Private Maven Repository

All projects share a common private Maven repository at `https://mvn.mckernant1.com/release` (backed by S3) for internal artifacts:

| Artifact | Group ID | Notes |
|---|---|---|
| `esports-api` | `com.mckernant1.lol` | Generated Java SDK from lol-openapi |
| `kotlin-utils` | `com.mckernant1` | Shared Kotlin utilities |
| `metrics` | `com.mckernant1.commons` | CloudWatch metrics library |

---

## Replication Checklist

To replicate or deploy this platform:

1. **AWS account** with DynamoDB, ECR, CloudWatch, and SSM Parameter Store in `us-west-2`
2. **ECR repositories** for the three Docker images (or push to a container registry of your choice)
3. **Leaguepedia bot credentials** — request at `https://lol.fandom.com/wiki/Special:BotPasswords`
4. **DynamoDB tables** — create `Leagues`, `Tournaments`, `Matches`, `Players`, `Teams` tables (keys documented in `leaguepedia-loader/src/models/`)
5. **Run `leaguepedia-loader`** to backfill tables from Leaguepedia (or set up a scheduled task/EventBridge rule)
6. **Deploy `lol-api-service`** to ECS/Fargate or similar — requires access to DynamoDB tables, an nginx reverse proxy config (provided in `config/nginx.conf`), and port 8080 exposed
7. **Update DNS** — point `api.lol-esports.mckernant1.com` (or your domain) at the service
8. **Regenerate SDKs** — run `openapi-generator-cli` against `lol-openapi/root.yaml` and publish to your Maven/crates.io/npm registry
9. **For `lol-predictions-bot`** — create `Predictions` and `UserSettings` DynamoDB tables, set all required env vars, deploy the Docker image, and register the bot with Discord via the Developer Portal
10. **Optional: deploy `lol-api-service-rust`** as a gRPC-only drop-in once the proto submodule is updated to match the Kotlin version

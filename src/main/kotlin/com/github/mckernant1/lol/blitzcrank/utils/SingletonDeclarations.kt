package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.blitzcrank.metrics.AWSCloudwatchMetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.MetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.NoMetricsMetricsPublisher
import com.github.mckernant1.lol.heimerdinger.config.EsportsApiConfig
import com.github.mckernant1.lol.heimerdinger.config.HostUrl
import com.github.mckernant1.lol.heimerdinger.leagues.LeagueClient
import com.github.mckernant1.lol.heimerdinger.schedule.ScheduleClient
import com.github.mckernant1.lol.heimerdinger.team.TeamClient
import com.github.mckernant1.lol.heimerdinger.tournaments.TournamentClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.*

private val esportsLogger: Logger = LoggerFactory.getLogger("EsportsAPIWrapper")
private val esportsApiConfig = EsportsApiConfig(
    endpointHost = HostUrl.ESPORTS_API_2
)

val leagueClient by lazy { LeagueClient(esportsApiConfig = esportsApiConfig) }
val tournamentClient by lazy { TournamentClient(esportsApiConfig = esportsApiConfig) }
val scheduleClient by lazy { ScheduleClient(esportsApiConfig = esportsApiConfig) }
val teamClient by lazy { TeamClient(esportsApiConfig = esportsApiConfig) }

private val dc by lazy { DynamoDbClient.builder().build() }
internal val ddbClient: DynamoDbEnhancedClient by lazy {
    DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dc)
        .build()
}

internal val cwp: MetricsPublisher by lazy {
    if (System.getenv("METRICS_ENABLED").equals("true", ignoreCase = true))
        AWSCloudwatchMetricsPublisher()
    else
        NoMetricsMetricsPublisher()
}

internal val globalTimer by lazy { Timer() }


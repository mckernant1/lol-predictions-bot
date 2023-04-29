package com.mckernant1.lol.blitzcrank.utils.migration

import com.mckernant1.lol.blitzcrank.model.Prediction
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.dc
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import kotlinx.coroutines.runBlocking
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.system.exitProcess

fun convertPredictions() = runBlocking {
    println("Loading Matches into Cache")
    val gson = GsonBuilder()
        .registerTypeAdapter(ZonedDateTime::class.java, object : TypeAdapter<ZonedDateTime>() {
            override fun write(out: JsonWriter?, value: ZonedDateTime?) {
                out?.value(value?.toInstant()?.toEpochMilli())
            }

            override fun read(`in`: JsonReader?): ZonedDateTime {
                return Instant.ofEpochMilli(`in`?.nextLong()!!).atZone(ZoneId.of("UTC"))
            }
        })
        .create()
//    val tClient = TournamentClient()
//    val sClient = ScheduleClient()
//    val matchesToWrite: Map<String, Match> = LeagueClient().getLeagues().asSequence()
//        .map { it to tClient.getTournamentsForLeague(it.id) }
//        .flatMap { (league, tournaments) ->
//            tournaments.flatMap {
//                try {
//                    sClient.getSplitByTournament(league.id, it).matches
//                } catch (e: Exception) {
//                    listOf(null)
//                }
//            }
//        }
//        .filterNotNull()
//        .associateBy { it.id }
//
//
//    File("matches.json").printWriter().use {
//        it.write(gson.toJson(matchesToWrite))
//    }
//
    val matches: JsonObject = gson.fromJson(
        File("matches.json").readText(),
        JsonObject::class.java
    )
    println("Matches API cache size: ${matches.keySet().size}")

    println("Loading new matches into cache")
    val items = dc.scanPaginator {
        it.tableName("Matches")
    }.items().toList()
    println("Matches Table cache size: ${items.size}")

    val teams = apiClient.teams

    println("Converting...")
    Prediction.scan().forEach { pred ->
        val match = matches[pred.matchId]?.asJsonObject ?: run {
            println("No Matches found for prediction ${pred.matchId}")
            return@forEach
        }
        println("Starting for $pred")
        items
            .filter {
                Instant.ofEpochMilli(it["startTime"]?.n()?.toLong() ?: 0)
                    .isAfter(Instant.ofEpochMilli(match["date"].asLong) - Duration.ofHours(12))
                        &&
                        Instant.ofEpochMilli(it["startTime"]?.n()?.toLong() ?: 0)
                            .isBefore(Instant.ofEpochMilli(match["date"].asLong) + Duration.ofHours(12))
            }.find {
                println(it)
                val teamIds: List<String?> = listOf(
                    convertTeam(match["team2"].asString),
                    convertTeam(match["team1"].asString)
                ).map { teamName ->
                    teams.find { team ->
                        team.name.equals(teamName, true)
                    }?.teamId
                }
                teamIds.contains(it["redTeamId"]?.s()!!) && teamIds.contains(it["blueTeamId"]?.s()!!)
                    .also { bool ->
                        println("$teamIds contains? ${it["redTeamId"]?.s()!!} and ${it["blueTeamId"]?.s()!!} => $bool")
                    }
            }?.let {
                println("Putting new Match")
                Prediction.putItem(
                    Prediction(pred.userId, it["matchId"]?.s()!!, teams.find {
                        convertTeam(pred.prediction).equals(it.name, true)
                    }?.teamId ?: error("Cannot find team for '${pred.prediction}'"))
                )
            } ?: run {
            println(match)
            println("Could not find newId for old id ${pred.matchId}")
            exitProcess(0)
        }
    }
}


fun convertTeam(team: String): String {
    return when (team) {
        "EXCEL" -> "Excel Esports"
        "Rouge" -> "Rogue (European Team)"
        "Schalke 04" -> "FC Schalke 04 Esports"
        "RED Kalunga" -> "RED Canids"
        "Galatasaray Espor" -> "Galatasaray Esports"
        "Mega Bank Beyond Gaming" -> "Beyond Gaming"
        "Unicorns of Love" -> "Unicorns of Love.CIS"
        "Dignitas QNTMPAY" -> "Dignitas"
        "Papara SuperMassive Blaze" -> "SuperMassive Esports"
        "Rainbow7 " -> "Rainbow7"
        "Machi Esports" -> "MachiX"
        "Weibo Gaming" -> "Suning"
        "Immortals Progressive" -> "Immortals"
        "fastPay Wildcats" -> "Istanbul Wildcats"
        "Anyone's Legend" -> "Rogue Warriors"
        "paiN Academy" -> "paiN Gaming Academy"
        "CrowCrowd Moscow" -> "CrowCrowd"
        "CTRL PLAY Team" -> "CTRL PLAY"
        "Mousesports" -> "MOUZ"
        "Papara SuperMassive" -> "SuperMassive Esports"
        "DAMWON Gaming" -> "DWG KIA"
        "Gillette Infinity" -> "INFINITY"
        "Afreeca Freecs" -> "Kwangdong Freecs"
        "eStar" -> "Ultra Prime"
        "Vorax Academy" -> "Liberty Academy"
        else -> team
    }
}

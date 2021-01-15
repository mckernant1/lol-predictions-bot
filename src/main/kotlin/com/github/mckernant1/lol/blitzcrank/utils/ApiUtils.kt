package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.heimerdinger.config.EsportsApiConfig
import com.github.mckernant1.lol.heimerdinger.config.HostUrl
import com.github.mckernant1.lol.heimerdinger.leagues.LeagueClient
import com.github.mckernant1.lol.heimerdinger.schedule.ScheduleClient
import com.github.mckernant1.lol.heimerdinger.team.TeamClient
import com.github.mckernant1.lol.heimerdinger.tournaments.TournamentClient
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val esportsLogger: Logger = LoggerFactory.getLogger("EsportsAPIWrapper")
private val esportsApiConfig = EsportsApiConfig(
    logger = { esportsLogger.debug(it) },
    endpointHost = HostUrl.ESPORTS_API_2
)

val leagueClient = LeagueClient(esportsApiConfig = esportsApiConfig)
val tournamentClient = TournamentClient(esportsApiConfig = esportsApiConfig)
val scheduleClient = ScheduleClient(esportsApiConfig = esportsApiConfig)
val teamClient = TeamClient(esportsApiConfig = esportsApiConfig)

private val connectionString: String = System.getenv("MONGO_STRING")
    ?: throw Exception("Environment variable MONGO_STRING has not been specified")


private val client = KMongo.createClient(connectionString)
val collection = client.getDatabase("lol-prediction").getCollection<Prediction>("discord-predictions")

data class Prediction (
    val matchId: String,
    val userId: String,
    val prediction: String
)


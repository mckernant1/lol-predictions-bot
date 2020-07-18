package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.config.EsportsApiConfig
import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.schedule.Split
import com.github.mckernant1.lolapi.tournaments.Standing
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import net.dv8tion.jda.api.entities.Message
import org.apache.http.impl.client.cache.CacheConfig
import java.time.Year
import java.util.*


val BOT_TOKEN: String = System.getenv("BOT_TOKEN") ?: throw Exception("BOT_TOKEN environment variable required")

private val cacheConfig: CacheConfig = CacheConfig.custom()
    .setSharedCache(false)
    .setHeuristicDefaultLifetime(3600)
    .build()
private val esportsApiConfig = EsportsApiConfig(cacheConfig = cacheConfig)

val leagueClient = LeagueClient(esportsApiConfig = esportsApiConfig)
val tournamentClient = TournamentClient(esportsApiConfig = esportsApiConfig)
val scheduleClient = ScheduleClient(esportsApiConfig = esportsApiConfig)

fun getSchedule(region: String, numberToGet: Int): List<Match> {
    return getMatches(region).matches.sortedBy { it.date }.dropWhile {
        it.date < Date()
    }.take(numberToGet)
}

fun getResults(region: String, numberToGet: Int): List<Match> {
    return getMatches(region).matches.sortedByDescending { it.date }.dropWhile {
        it.date > Date()
    }.take(numberToGet)
}

fun getStandings(region: String): List<Standing> {
    val league = leagueClient.getLeagueByName(region)
    return tournamentClient.getStandingsForLeague(league.id, Year.now().value)
}

fun getMatches(region: String): Split {
    val league = leagueClient.getLeagueByName(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    return scheduleClient.getSplitByTournament(league.id, tourney)
}

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

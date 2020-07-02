package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.config.EsportsApiConfig
import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.schedule.Split
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.apache.http.impl.client.cache.CacheConfig
import java.util.*


val BOT_TOKEN: String = System.getenv("BOT_TOKEN") ?: "NDc2MjE3ODk5OTA0OTI1Njk2.Xvj0Nw.hTwdhUgGfRNn58vtvIXRczIRiLM"

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

fun getResults(region: String, numberToGet: Int): List<Match>  {
    return getMatches(region).matches.sortedByDescending { it.date }.dropWhile {
        it.date > Date()
    }.take(numberToGet)
}

fun getMatches(region: String): Split {
    val league = leagueClient.getLeagueByName(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    return scheduleClient.getSplitByTournament(league.id, tourney)
}

fun reactInternalError(m: Message) {
    m.addReaction("❌").complete()
}

fun reactUserError(m: Message) {
    m.addReaction("⛔").complete()
}


fun validateToRegionAndNumberOfGames(
    words: List<String>,
    event: MessageReceivedEvent
): Pair<String, Int>? {
    val message = event.message
    if (words.size < 2) {
        reactUserError(message)
        return null
    }
    return try {
        val getNumber = (words.getOrNull(2) ?: "3").toInt()
        val region = words[1].toUpperCase()
        if (!leagueClient.getLeagues().map { it.name }.contains(region)) {
            reactUserError(message)
            return null
        }
        message.addReaction("\uD83D\uDC4C").complete()
        Pair(region, getNumber)
    } catch (e: Exception) {
        reactInternalError(message)
        null
    }

}

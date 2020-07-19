package com.github.mckernant1.runner.utils

import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.lolapi.schedule.Split
import com.github.mckernant1.lolapi.tournaments.Standing
import net.dv8tion.jda.api.entities.Message
import java.time.Year
import java.util.*


val BOT_TOKEN: String = System.getenv("BOT_TOKEN") ?: throw Exception("BOT_TOKEN environment variable required")

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

fun getLeagues() = leagueClient.getLeagues()

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

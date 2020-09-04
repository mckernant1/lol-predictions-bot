package com.github.mckernant1.runner.utils

import com.github.mckernant1.fs.FileHandler
import com.github.mckernant1.fs.startJobThread
import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.lolapi.schedule.Split
import com.github.mckernant1.lolapi.tournaments.Standing
import net.dv8tion.jda.api.entities.Message
import java.time.Duration
import java.time.Year
import java.util.*


val BOT_TOKEN: String = System.getenv("BOT_TOKEN") ?: throw Exception("BOT_TOKEN environment variable required")

val fileHandler = FileHandler(
    duration = Duration.ofMinutes(60),
    logger = System.out
)

val schedules = mutableMapOf<String, Thread>()

fun getSchedule(region: String, numberToGet: Int): List<Match> {
    return getMatchesWithThreads(region).matches
        .sortedBy { it.date }.dropWhile {
            it.date < Date()
        }.take(numberToGet)
}

fun getScheduleForNextDay(region: String): List<Match> {
    return getMatchesWithThreads(region).matches
        .filter {
            val date = it.date
            val today = Date()
            date.date == today.date
        }
}

fun getResults(region: String, numberToGet: Int): List<Match> {
    return getMatchesWithThreads(region).matches
        .sortedByDescending { it.date }.dropWhile {
            it.date > Date()
        }.take(numberToGet)
}

fun getMatchesWithThreads(region: String): Split {
    val scheduleId = "$region-schedule"
    val split = fileHandler.getResult(scheduleId) {
        getMatches(region)
    }
    if (!schedules.containsKey(scheduleId)) {
        println("Starting Thread for retrieving $scheduleId")
        schedules[scheduleId] = startJobThread(Duration.ofHours(1)) {
            fileHandler.getResult(scheduleId) {
                getMatches(region)
            }
        }
    }
    return split
}

fun getStandings(region: String): List<Standing> {

    val league = leagueClient.getLeagueByName(region)
    val leagueId = "${league.id}-standings"

    val standings = fileHandler.getResult(leagueId) {
        val list = arrayListOf<Standing>()
        list.addAll(tournamentClient.getStandingsForLeague(league.id, Year.now().value))
        list
    }
    if (!schedules.containsKey(leagueId)) {
        println("Starting Thread for retrieving $leagueId")
        schedules[leagueId] = startJobThread(Duration.ofMinutes(5)) {
            fileHandler.getResult(leagueId) {
                val list = arrayListOf<Standing>()
                list.addAll(tournamentClient.getStandingsForLeague(league.id, Year.now().value))
                list
            }
        }
    }

    return standings
}

fun getMatches(region: String): Split {
    val league = leagueClient.getLeagueByName(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    return scheduleClient.getSplitByTournament(league.id, tourney)
}

fun getLeagues() = leagueClient.getLeagues()

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

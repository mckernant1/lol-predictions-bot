package com.github.mckernant1.lol.blitzcrank.utils


import com.github.mckernant1.fs.TimedFileCache
import com.github.mckernant1.fs.startJobThread
import com.github.mckernant1.lol.heimerdinger.schedule.Match
import com.github.mckernant1.lol.heimerdinger.schedule.Split
import com.github.mckernant1.lol.heimerdinger.tournaments.Standing
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Year
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Timer
import java.util.TimerTask

private val fileCacheLogger: Logger = LoggerFactory.getLogger("FileCacheLogger")
private val retrievalLogger = LoggerFactory.getLogger("RetrievalLogger")
private val fileHandler = TimedFileCache(
    duration = Duration.ofMinutes(60),
    logger = { fileCacheLogger.info(it) }
)

private val jobTimer = Timer()
val schedules = mutableMapOf<String, TimerTask>()

fun getSchedule(region: String, numberToGet: Int?): List<Match> {
    val matches = getMatchesWithThreads(region).matches
        .sortedBy { it.date }.dropWhile {
            it.date < ZonedDateTime.now(ZoneId.of("UTC"))
        }
    retrievalLogger.info("matches: $matches")
    if (matches.isEmpty()) return emptyList()

    return if (numberToGet == null) {
        val first = matches.first()
        matches.takeWhile {
            it.date.isBefore(first.date + Duration.ofHours(12))
        }
    } else {
        matches.take(numberToGet)
    }
}

fun getResults(region: String, numberToGet: Int?): List<Match> {
    val matches = getMatchesWithThreads(region).matches
        .sortedByDescending { it.date }.dropWhile {
            it.date > ZonedDateTime.now(ZoneId.of("UTC"))
        }

    if (matches.isEmpty()) return emptyList()

    return if (numberToGet == null) {
        val first = matches.first()
        matches.takeWhile {
            it.date.isAfter(first.date - Duration.ofHours(12))
        }
    } else {
        matches.take(numberToGet)
    }
}

fun getMatchesWithThreads(region: String): Split {
    val scheduleId = "$region-schedule"
    val split = fileHandler.getResult(scheduleId) {
        getMatches(region)
    }
    if (!schedules.containsKey(scheduleId)) {
        println("Starting Thread for retrieving $scheduleId")
        schedules[scheduleId] = jobTimer.startJobThread(Duration.ofHours(1)) {
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
        schedules[leagueId] = jobTimer.startJobThread(Duration.ofMinutes(5)) {
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

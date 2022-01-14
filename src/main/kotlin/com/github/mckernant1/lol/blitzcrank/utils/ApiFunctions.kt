package com.github.mckernant1.lol.blitzcrank.utils


import com.github.mckernant1.fs.TimedFileCache
import com.github.mckernant1.fs.startJobThread
import com.github.mckernant1.lol.heimerdinger.schedule.Match
import com.github.mckernant1.lol.heimerdinger.schedule.Split
import com.github.mckernant1.lol.heimerdinger.team.Team
import com.github.mckernant1.lol.heimerdinger.tournaments.Standing
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

private val fileCacheLogger: Logger = LoggerFactory.getLogger("FileCacheLogger")
private val retrievalLogger = LoggerFactory.getLogger("RetrievalLogger")
private val fileHandler = TimedFileCache(
    duration = Duration.ofMinutes(30),
    logger = { fileCacheLogger.info(it) }
)

private val jobTimer = Timer()
val schedules = mutableMapOf<String, TimerTask>()

fun getSchedule(region: String, numberToGet: Int?): List<Match> {
    val matches = getMatchesWithThreads(region).matches
        .sortedBy { it.date }.dropWhile {
            it.date < ZonedDateTime.now(ZoneId.of("UTC"))
        }
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
        .filter { it.winner == it.team1 || it.winner == it.team2}
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
        fileCacheLogger.info("Starting Thread for retrieving $scheduleId")
        schedules[scheduleId] = jobTimer.startJobThread(Duration.ofMinutes(30)) {
            try {
                fileHandler.getResult(scheduleId) {
                    getMatches(region)
                }
            } catch(e: Exception) {
                fileCacheLogger.warn("Hit error getting matches", e)
            }
        }
    }
    return split
}

fun getStandings(region: String): List<Standing> {
    val league = leagueClient.getLeagueBySlug(region)
    return tournamentClient.getStandingsForMostRecentTournamentInLeague(league.id)
}

fun getTeamFromName(teamName: String): Team {
    return teamClient.getTeamByCode(teamName)
}

fun getMatches(region: String): Split {
    val league = leagueClient.getLeagueBySlug(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    return scheduleClient.getSplitByTournament(league.id, tourney)
}

fun getTeams() = teamClient.getAllTeams()

fun getLeagues() = leagueClient.getLeagues()

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

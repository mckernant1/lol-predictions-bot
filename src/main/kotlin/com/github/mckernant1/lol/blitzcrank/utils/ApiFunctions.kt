package com.github.mckernant1.lol.blitzcrank.utils


import com.github.mckernant1.fs.TimedFileCache
import com.github.mckernant1.lol.esports.api.Match
import com.github.mckernant1.lol.heimerdinger.team.Team
import com.github.mckernant1.lol.heimerdinger.tournaments.Standing
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

private val fileCacheLogger: Logger = LoggerFactory.getLogger("FileCacheLogger")
private val retrievalLogger = LoggerFactory.getLogger("RetrievalLogger")
private val fileHandler = TimedFileCache(
    duration = Duration.ofMinutes(30),
    logger = { fileCacheLogger.info(it) }
)

fun getSchedule(region: String, numberToGet: Int?): List<Match> {
    val mostRecentTourney = apiClient.getMostRecentTournament(region)
    val matches = apiClient.getMatchesForTournament(mostRecentTourney.tournamentId)
        .sortedBy { it.startTimeAsInstant() }.dropWhile {
            it.startTimeAsInstant() < Instant.now()
        }
    if (matches.isEmpty()) return emptyList()

    return if (numberToGet == null) {
        val first = matches.first()
        matches.takeWhile {
            it.startTimeAsInstant().isBefore(first.startTimeAsInstant() + Duration.ofHours(12))
        }
    } else {
        matches.take(numberToGet)
    }
}

fun getResults(region: String, numberToGet: Int?): List<Match> {
    val mostRecentTourney = apiClient.getMostRecentTournament(region)
    val matches = apiClient.getMatchesForTournament(mostRecentTourney.tournamentId)
        .filter { it.winner == it.blueTeamId || it.winner == it.redTeamId }
        .sortedByDescending { it.startTimeAsInstant() }.dropWhile {
            it.startTimeAsInstant() > Instant.now()
        }

    if (matches.isEmpty()) return emptyList()

    return if (numberToGet == null) {
        val first = matches.first()
        matches.takeWhile {
            it.startTimeAsInstant().isAfter(first.startTimeAsInstant() - Duration.ofHours(12))
        }
    } else {
        matches.take(numberToGet)
    }
}

fun getStandings(region: String): List<Standing> {
    val league = leagueClient.getLeagueBySlug(region)
    return tournamentClient.getStandingsForMostRecentTournamentInLeague(league.id)
}

fun getTeamFromName(teamName: String): Team {
    return teamClient.getTeamByCode(teamName)
}

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

package com.mckernant1.lol.blitzcrank.utils


import com.mckernant1.lol.blitzcrank.utils.model.Standing
import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.commons.standalone.measureOperation
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger("ApiFunctions")

fun getSchedule(region: String, numberToGet: Int?): List<Match> {
    val (getMostRecentTournamentDuration, mostRecentTourney) = measureOperation {
        apiClient.getMostRecentTournament(region.uppercase())
    }
    logger.info("GetMostRecentTournament took ${getMostRecentTournamentDuration.toMillis()}ms")

    val (getMatchesDuration, matches) = measureOperation {
        apiClient.getMatchesForTournament(mostRecentTourney.tournamentId)
            .sortedBy { it.startTimeAsInstant() }.dropWhile {
                it.startTimeAsInstant() < Instant.now()
            }
    }
    logger.info("GetMatches took ${getMatchesDuration.toMillis()}ms")

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
    val (getMostRecentTournamentDuration, mostRecentTourney) = measureOperation {
        apiClient.getMostRecentTournament(region.uppercase())
    }
    logger.info("GetMostRecentTournament took ${getMostRecentTournamentDuration.toMillis()}ms")

    val (getMatchesDuration, matches) = measureOperation {
        apiClient.getMatchesForTournament(mostRecentTourney.tournamentId)
            .filter { it.winner == it.blueTeamId || it.winner == it.redTeamId }
            .sortedByDescending { it.startTimeAsInstant() }.dropWhile {
                it.startTimeAsInstant() > Instant.now()
            }
    }
    logger.info("GetMatches took ${getMatchesDuration.toMillis()}ms")

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
    val tourney = apiClient.getMostRecentTournament(region.uppercase())
    val matches = apiClient.getMatchesForTournament(tourney.tournamentId)
        .filterPastMatches()
        .filter { it.winner != null }

    val standings: Map<String, Standing> = matches.flatMap { listOf(it.blueTeamId, it.redTeamId) }
        .associateWith { Standing(it) }
    matches.forEach {
        standings[it.winner]!!.wins.add(it.getLoser())
        standings[it.getLoser()]!!.losses.add(it.winner!!)
    }
    return standings.values.toList()
}

fun getWordsFromMessage(message: Message): List<String> = getWordsFromString(message.contentRaw)

fun getWordsFromString(string: String): List<String> = string.replace("\\s+".toRegex(), " ").split(" ")

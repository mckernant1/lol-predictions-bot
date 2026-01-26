package com.mckernant1.lol.blitzcrank.utils


import com.mckernant1.lol.blitzcrank.utils.model.Standing
import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.commons.standalone.measureOperation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant

private val logger: Logger = LoggerFactory.getLogger("ApiFunctions")

suspend fun getSchedule(
    region: String,
    numberToGet: Int?,
    filter: ((Match) -> Boolean)? = null,
): List<Match> {
    var (getMatchesDuration, matchesSeq) = measureOperation {
        withContext(Dispatchers.IO) {
            apiClient.getMatches(region.uppercase(), null)
        }.asSequence()
            .sortedBy { it.startTimeAsInstant() }
            .dropWhile {
                it.startTimeAsInstant() < Instant.now()
            }
    }

    if (filter != null) {
        matchesSeq = matchesSeq.filter(filter)
    }

    logger.info("GetMatches took ${getMatchesDuration.toMillis()}ms")

    val matches = matchesSeq.toList()

    if (matches.isEmpty()) {
        logger.info("There are no matches returned for region: ${region.uppercase()}")
        return emptyList()
    }

    return if (numberToGet == null) {
        val first = matches.first()
        matches.takeWhile {
            it.startTimeAsInstant().isBefore(first.startTimeAsInstant() + Duration.ofHours(12))
        }
    } else {
        matches.take(numberToGet)
    }
}

suspend fun getResults(region: String, numberToGet: Int?): List<Match> {
    val (getMatchesDuration, matches) = measureOperation {
        withContext(Dispatchers.IO) {
            apiClient.getMatches(region.uppercase(), null)
        }.filter { it.winner == it.blueTeamId || it.winner == it.redTeamId }
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

suspend fun getStandings(region: String): List<Standing> {
    val tourney = withContext(Dispatchers.IO) {
        apiClient.getMostRecentTournament(region.uppercase())
    }
    val matches = withContext(Dispatchers.IO) {
        apiClient.getMatchesForTournament(tourney.tournamentId)
    }
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

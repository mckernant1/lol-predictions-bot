package com.github.mckernant1.lol.blitzcrank.utils


import com.github.mckernant1.lol.blitzcrank.utils.model.Standing
import com.github.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.entities.Message
import java.time.Duration
import java.time.Instant

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
    val tourney = apiClient.getMostRecentTournament(region)
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

fun getWordsFromMessage(message: Message) = message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.esports.api.Match
import com.github.mckernant1.lol.esports.api.Tournament
import org.openapitools.client.api.DefaultApi
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

private val apiLogger = LoggerFactory.getLogger("ApiLogger")

fun DefaultApi.getMostRecentTournament(league: String): Tournament {
    return this.getTournamentsForLeague(league.toUpperCase())
        .filter { it.startDateAsDate()?.before(Date.from(Instant.now())) ?: false }
        .maxByOrNull { it.startDateAsDate()!! }
        ?: error("Could not get a tournament for league '$league'")
}

fun List<Match>.filterPastMatches() = filter {
    it.startTimeAsInstant().isBefore(Instant.now())
}

fun List<Match>.filterFutureMatches() = filter {
    it.startTimeAsInstant().isAfter(Instant.now())
}

fun Tournament.startDateAsDate(): Date? {
    return try {
        SimpleDateFormat("yyyy-MM-dd").parse(this.startDate)
    } catch (e: Exception) {
        null
    }
}

fun Match.startTimeAsInstant(): Instant =
    Instant.ofEpochMilli(this.startTime.longValueExact())


fun Match.getLoser(): String = when (winner) {
    blueTeamId -> redTeamId
    redTeamId -> blueTeamId
    else -> "TBD"
}

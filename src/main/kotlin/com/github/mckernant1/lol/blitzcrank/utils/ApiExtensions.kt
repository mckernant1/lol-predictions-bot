package com.github.mckernant1.lol.blitzcrank.utils


import com.github.mckernant1.lol.esports.api.client.DefaultApi
import com.github.mckernant1.lol.esports.api.models.Match
import com.github.mckernant1.lol.esports.api.models.Tournament
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date

private val apiLogger = LoggerFactory.getLogger("ApiLogger")

fun DefaultApi.getMostRecentTournament(league: String): Tournament {
    val tournamentsByMostRecent = this.getTournamentsForLeague(league.uppercase())
        .filter {
            it.startDateAsDate()
                ?.minus(7, ChronoUnit.DAYS)
                ?.isBefore(Instant.now())
                ?: false
        }.sortedByDescending { it.startDateAsDate()!! }

    val mostRecentTournament = tournamentsByMostRecent.find { it.isOngoing() }
        ?: tournamentsByMostRecent.first()

    apiLogger.info("Most recent tournament for ${league.uppercase()} is ${mostRecentTournament.tournamentId}")

    return mostRecentTournament
}

fun List<Match>.filterPastMatches() = filter {
    it.startTimeAsInstant().isBefore(Instant.now())
}

fun List<Match>.filterFutureMatches() = filter {
    it.startTimeAsInstant().isAfter(Instant.now())
}

fun Tournament.isOngoing(): Boolean {
    val now = Instant.now().truncatedTo(ChronoUnit.DAYS)
    return this.startDateAsDate().orEpoch() <= now && now <= this.endDateAsDate().orEpoch()
}

fun Tournament.startDateAsDate(): Instant? {
    return try {
        SimpleDateFormat("yyyy-MM-dd").parse(this.startDate).toInstant()
    } catch (e: Exception) {
        null
    }
}

private fun Instant?.orEpoch(): Instant = this ?: Instant.EPOCH

fun Tournament.endDateAsDate(): Instant? {
    return try {
        SimpleDateFormat("yyyy-MM-dd").parse(this.endDate).toInstant()
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

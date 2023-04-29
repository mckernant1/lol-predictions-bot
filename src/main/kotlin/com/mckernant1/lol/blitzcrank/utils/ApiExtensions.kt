package com.mckernant1.lol.blitzcrank.utils


import com.mckernant1.lol.esports.api.models.Match
import com.mckernant1.lol.esports.api.models.Tournament
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit

private val apiLogger = LoggerFactory.getLogger("ApiLogger")

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

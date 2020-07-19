package com.github.mckernant1.runner.commands

import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.runner.utils.getSchedule
import com.github.mckernant1.runner.utils.reactUserOk
import com.github.mckernant1.runner.utils.validateAndParseRegionAndNumberForResultsAndSchedule
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.text.DateFormat
import java.time.ZoneId
import java.util.*

fun scheduleCmd(
    event: MessageReceivedEvent
) {
    val (region, numToGet) = validateAndParseRegionAndNumberForResultsAndSchedule(
        event
    ) ?: return

    reactUserOk(event.message)
    val matches = getSchedule(region, numToGet)
    val replyString =
        formatScheduleReply(matches, numToGet, region)
    event.channel.sendMessage(replyString).complete()
}


private fun formatScheduleReply(matches: List<Match>, numToGet: Int, region: String): String {
    val sb = StringBuilder()
    val dateFormat = DateFormat.getDateTimeInstance(
        DateFormat.FULL, DateFormat.LONG
    )
    dateFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("America/Los_Angeles"))
    sb.appendln("The next $numToGet matches in $region are: ")
    matches.forEach {
        sb.appendln("${dateFormat.format(it.date)}: ${it.team1} vs ${it.team2}")
    }
    return sb.toString()
}


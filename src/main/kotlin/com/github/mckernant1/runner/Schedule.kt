package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.text.DateFormat

fun scheduleCmd(
    words: List<String>,
    event: MessageReceivedEvent
) {
    val (region, numToGet) = validateToRegionAndNumberOfGames(words, event) ?: return
    val matches = getSchedule(region, numToGet)
    val replyString = formatReply(matches, numToGet, region)
    event.channel.sendMessage(replyString).complete()
}


private fun formatReply(matches: List<Match>, numToGet: Int, region: String): String {
    val sb = StringBuilder()
    val dateFormat = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.LONG)
    sb.appendln("The next $numToGet matches in $region are: ")
    matches.forEach {
        sb.appendln("${dateFormat.format(it.date)}: ${it.team1} vs ${it.team2}")
    }
    return sb.toString()
}


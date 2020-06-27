package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun scheduleCmd(
    words: List<String>,
    event: MessageReceivedEvent
) {
    val (region, numToGet) = validate(words, event) ?: return
    val matches = getSchedule(region, numToGet)
    val replyString = formatReply(matches, numToGet, region)
    event.channel.sendMessage(replyString).complete()
}


fun formatReply(matches: List<Match>, numToGet: Int, region: String): String {
    val sb = StringBuilder()
    sb.appendln("The next $numToGet matches in $region are: ")
    matches.forEach {
        sb.appendln("${it.team1} vs ${it.team2}")
    }
    return sb.toString()
}

fun validate(
    words: List<String>,
    event: MessageReceivedEvent
): Pair<String, Int>? {
    if (words.size < 2) {
        event.message.addReaction("⛔").complete()
        return null
    }
    return try {
        val getNumber = (words.getOrNull(2) ?: "3").toInt()
        val region = words[1].toUpperCase()
        event.message.addReaction("\uD83D\uDC4C").complete()
        Pair(region, getNumber)
    } catch (e: Exception) {
        event.message.addReaction("❌").complete()
        null
    }

}

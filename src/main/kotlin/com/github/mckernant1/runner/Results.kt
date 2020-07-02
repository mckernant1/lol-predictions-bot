package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun resultsCMD(
    words: List<String>,
    event: MessageReceivedEvent
) {
    val (region, numToGet) = validateToRegionAndNumberOfGames(words, event) ?: return
    val matches = getResults(region, numToGet)
    val replyString = formatResultsReply(matches, numToGet, region)
    event.channel.sendMessage(replyString).complete()
}

private fun formatResultsReply(matches: List<Match>, numToGet: Int, region: String): String {
    val sb = StringBuilder()
    sb.appendln("The last $numToGet matches in $region were: ")
    matches.forEach {
        sb.appendln("${if (it.winner == it.team1) 
            "\uD83D\uDC51 " else ""}${it.team1} vs ${it.team2}${if (it.winner == it.team2) 
            " \uD83D\uDC51" else ""}")
    }
    return sb.toString()
}


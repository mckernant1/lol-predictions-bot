package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun resultsCMD(
    event: MessageReceivedEvent
) {
    val (region, numToGet) = validateAndParseRegionAndNumberForResultsAndSchedule(event) ?: return
    reactUserOk(event.message)
    val matches = getResults(region, numToGet)
    val replyString = formatResultsReply(matches, numToGet, region)
    event.channel.sendMessage(replyString).complete()
}

private fun formatResultsReply(matches: List<Match>, numToGet: Int, region: String): String {
    val sb = StringBuilder()
    sb.appendln("The last $numToGet matches in $region were: ")
    matches.forEach {
        sb.appendln("${if (it.winner == it.team1) 
            "\uD83D\uDC51 " else ""}${it.team1} (${it.team1NumWins}) vs (${it.team2NumWins}) ${it.team2}${if (it.winner == it.team2) 
            " \uD83D\uDC51" else ""}")
    }
    return sb.toString()
}


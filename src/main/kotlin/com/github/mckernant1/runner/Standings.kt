package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.tournaments.Standing
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun standingsCMD(
    words: List<String>,
    event: MessageReceivedEvent
) {
    val region = validateRegionAndWordCount(words, event) ?: return
    val standings = getStandings(region)
    val replyString = formatStandingsReply(standings, region)
    event.channel.sendMessage(replyString).complete()
}

fun formatStandingsReply(
    standings: List<Standing>,
    region: String
): String {
    val sb = StringBuilder()
    sb.appendln("Standings for $region:")
    standings.sortedByDescending { it.wins }.forEach {
        sb.appendln("${it.teamName}: ${it.wins}-${it.losses}")
    }
    return sb.toString()
}




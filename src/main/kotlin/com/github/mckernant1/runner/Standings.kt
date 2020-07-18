package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.tournaments.Standing
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun standingsCMD(
    event: MessageReceivedEvent
) {
    val message = event.message
    val words = getWordsFromMessage(message)
    if (!validateWordCount(words) { it == 2 }) {
        reactUserError(message)
        return
    }

    val region = getRegion(message)

    if (!validateRegion(region)) {
        reactUserError(message)
        return
    }
    reactUserOk(message)

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
        if (it.teamName != "TBD") {
            sb.appendln("${it.teamName}: ${it.wins}-${it.losses}")
        }
    }
    return sb.toString()
}




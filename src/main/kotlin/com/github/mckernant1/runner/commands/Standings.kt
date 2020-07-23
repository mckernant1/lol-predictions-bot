package com.github.mckernant1.runner.commands

import com.github.mckernant1.lolapi.tournaments.Standing
import com.github.mckernant1.runner.utils.*
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
    event.channel.sendMessage(replyString).queue()
}

fun formatStandingsReply(
    standings: List<Standing>,
    region: String
): String {
    val sb = StringBuilder()
    sb.appendln("Standings for $region:")
    return standings.sortedByDescending { it.wins / it.losses.toDouble() }.fold(sb) { stringBuilder: StringBuilder, standing: Standing ->
        if (standing.teamName != "TBD") {
            stringBuilder.appendln("${standing.teamName}: ${standing.wins}-${standing.losses}")
        }
        return@fold stringBuilder
    }.toString()
}




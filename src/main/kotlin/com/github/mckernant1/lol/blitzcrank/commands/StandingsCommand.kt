package com.github.mckernant1.lol.blitzcrank.commands


import com.github.mckernant1.lol.blitzcrank.utils.getStandings
import com.github.mckernant1.lol.heimerdinger.tournaments.Standing
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class StandingsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val standings = getStandings(region)
        val replyString = formatStandingsReply(standings, region)
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2) && validateRegion(event, 1)
    }


    private fun formatStandingsReply(
        standings: List<Standing>,
        region: String
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Standings for $region:")
        return standings.sortedByDescending { it.wins / it.losses.toDouble() }
            .fold(sb) { stringBuilder: StringBuilder, standing: Standing ->
                if (standing.teamName != "TBD") {
                    stringBuilder.appendLine("${standing.teamName}: ${standing.wins}-${standing.losses}")
                }
                return@fold stringBuilder
            }.toString()
    }

}






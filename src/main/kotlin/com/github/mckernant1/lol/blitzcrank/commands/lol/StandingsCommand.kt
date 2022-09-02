package com.github.mckernant1.lol.blitzcrank.commands.lol


import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getStandings
import com.github.mckernant1.lol.blitzcrank.utils.model.Standing
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class StandingsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override fun execute() {
        val standings = getStandings(region)
        val replyString = formatStandingsReply(standings, region)
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate() {
        validateWordCount(2..2)
        validateRegion(1)
    }


    private fun formatStandingsReply(
        standings: List<Standing>,
        region: String,
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Standings for $region:")
        return standings.sortedByDescending { it.wins.size / it.losses.size.toDouble() }
            .fold(sb) { stringBuilder: StringBuilder, standing: Standing ->
                if (standing.teamCode != "TBD") {
                    stringBuilder.appendLine("${standing.teamCode}: ${standing.wins.size}-${standing.losses.size}")
                }
                return@fold stringBuilder
            }.toString()
    }

}






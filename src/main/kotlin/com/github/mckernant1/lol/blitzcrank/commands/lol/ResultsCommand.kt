package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ResultsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override fun execute() {
        val matches = getResults(region, numToGet)
        if (matches.isEmpty()) {
            val message = "There are no results"
            event.channel.sendMessage(message).complete()
            return
        }
        val replyString =
            formatResultsReply(matches, region)
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate() {
        validateWordCount(2..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    private fun formatResultsReply(matches: List<Match>, region: String): String {
        val sb = StringBuilder()
        sb.appendLine("The last ${matches.size} matches in ${region.uppercase()} were: ")
        matches.forEach {
            sb.appendLine(
                "${
                    if (it.winner == it.blueTeamId)
                        "\uD83D\uDC51 " else ""
                }**${it.blueTeamId}** vs **${it.redTeamId}**${
                    if (it.winner == it.redTeamId)
                        " \uD83D\uDC51" else ""
                }"
            )
        }
        return sb.toString()
    }
}



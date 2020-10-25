package com.github.mckernant1.runner.commands

import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.runner.utils.getResults
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ResultsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override suspend fun execute() {
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

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)
    }

    private fun formatResultsReply(matches: List<Match>, region: String): String {
        val sb = StringBuilder()
        sb.appendLine("The last ${matches.size} matches in $region were: ")
        matches.forEach {
            sb.appendLine(
                "${
                    if (it.winner == it.team1)
                        "\uD83D\uDC51 " else ""
                }**${it.team1}** (${it.team1NumWins}) vs (${it.team2NumWins}) **${it.team2}**${
                    if (it.winner == it.team2)
                        " \uD83D\uDC51" else ""
                }"
            )
        }
        return sb.toString()
    }
}



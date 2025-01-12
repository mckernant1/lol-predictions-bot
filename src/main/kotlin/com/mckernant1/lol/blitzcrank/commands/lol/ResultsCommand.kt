package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class ResultsCommand(event: CommandInfo) : DiscordCommand(event) {

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
            if (it.winner == it.blueTeamId) {
                sb.append("$CROWN ")
            }
            sb.append("**${it.blueTeamId}** vs **${it.redTeamId}**)")
            if (it.winner == it.redTeamId) {
                sb.append(" $CROWN")
            }

            if (it.vod != null) {
                sb.append(" [Vod](${it.vod})")
            }

            if (it.highlight != null) {
                sb.append(" [Highlight](${it.highlight})")
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    companion object : CommandMetadata {
        private const val CROWN = "\uD83D\uDC51"
        override val commandString: String = "results"
        override val commandDescription: String = "The results for the given league"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription",
                  "options": [
                    {
                      "name": "league_id",
                      "description": "The league to query",
                      "type": 3,
                      "required": true
                    },
                    {
                      "name": "number_of_matches",
                      "description": "The number of matches to get",
                      "type": 3,
                      "required": false
                    }
                  ]
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = ResultsCommand(event)
    }
}



package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class ResultsCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

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

    companion object : CommandMetadata {
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



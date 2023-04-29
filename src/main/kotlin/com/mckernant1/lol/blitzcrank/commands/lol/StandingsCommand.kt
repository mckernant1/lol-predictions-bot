package com.mckernant1.lol.blitzcrank.commands.lol


import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.mckernant1.lol.blitzcrank.utils.getStandings
import com.mckernant1.lol.blitzcrank.utils.model.Standing
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class StandingsCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

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

    companion object : CommandMetadata {
        override val commandString: String = "standings"
        override val commandDescription: String = "The standings in the given league"
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
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = StandingsCommand(event)
    }

}






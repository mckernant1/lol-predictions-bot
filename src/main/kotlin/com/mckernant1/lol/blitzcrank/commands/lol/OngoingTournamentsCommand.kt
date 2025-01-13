package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class OngoingTournamentsCommand(event: CommandInfo) : DiscordCommand(event) {
    override fun execute() {
        val ongoingTourneys = apiClient.ongoingTournanments
            .map { apiClient.getLeagueByCode(it.leagueId) }
        val ongoingTourneyString = if (ongoingTourneys.isEmpty()) {
            "There are no ongoing tournaments"
        } else {
            ongoingTourneys
                .distinctBy { it.leagueId }
                .joinToString("\n") {
                    "- [**${it.leagueId}**] ${it.leagueName}"
                }
        }

        val messageToSend = """
            **Leagues with ongoing tournaments**:
$ongoingTourneyString
        """.trim()

        event.channel.sendMessage(messageToSend).complete()
    }

    override fun validate(options: Map<String, String>) = Unit

    companion object : CommandMetadata {
        override val commandString: String = "ongoing"
        override val commandDescription: String = "Lists the ongoing tournaments"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription"
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = OngoingTournamentsCommand(event)
    }
}

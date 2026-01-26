package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.commands.reminder.RemoveReminderCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class OngoingTournamentsCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {
    override suspend fun execute() {
        val ongoingTourneys = withContext(Dispatchers.IO) {
            apiClient.ongoingTournanments
        }.map { apiClient.getLeagueByCode(it.leagueId) }
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

        event.channel.sendMessage(messageToSend).submit().await()
    }

    override suspend fun validate(options: Map<String, String>) = Unit

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

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand =
            OngoingTournamentsCommand(event, userSettings)
    }
}

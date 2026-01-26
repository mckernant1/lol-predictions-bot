package com.mckernant1.lol.blitzcrank.commands.reminder

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class ListRemindersCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {

    override suspend fun execute() {
        event.channel.sendMessage("""
            Your Reminders
            ${userSettings.reminders.joinToString("-----------------") {
            """
            League: ${it.leagueSlug}
            Hours before: ${it.hoursBeforeMatches}
            """ }
            }
        """.trimIndent()).submit().await()
    }

    override suspend fun validate(options: Map<String, String>) {}

    companion object : CommandMetadata {
        override val commandString: String = "list-reminders"
        override val commandDescription: String = "List your reminders"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription"
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand = ListRemindersCommand(event, userSettings)    }
}

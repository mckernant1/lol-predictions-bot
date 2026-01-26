package com.mckernant1.lol.blitzcrank.commands.reminder

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.Reminder
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class AddReminderCommand(
    event: CommandInfo,
    userSettings: UserSettings
) : DiscordCommand(event, userSettings) {
    override suspend fun execute() {
        val newReminders = userSettings.reminders
        newReminders.add(
            Reminder(
                event.author.id,
                region,
                event.options["hours_before"]!!.toLong()
            )
        )
        userSettings.reminders = newReminders
        UserSettings.putSettings(userSettings)
        logger.info("Rem: $userSettings")
        event.channel.sendMessage("Your Reminder has been added").submit().await()
    }

    override suspend fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
        validateAndSetNumberPositive(options["hours_before"])
    }

    companion object : CommandMetadata {
        override val commandString: String = "add-reminder"
        override val commandDescription: String = "Add a reminder about upcoming games"
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
                      "name": "hours_before",
                      "description": "The number of hours before to remind",
                      "type": 3,
                      "required": true
                    }
                  ]
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand = AddReminderCommand(event, userSettings)    }
}

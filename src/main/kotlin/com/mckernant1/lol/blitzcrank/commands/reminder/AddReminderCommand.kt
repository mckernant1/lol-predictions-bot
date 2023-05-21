package com.mckernant1.lol.blitzcrank.commands.reminder

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.Reminder
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class AddReminderCommand(event: CommandInfo) : DiscordCommand(event) {
    override fun execute() {
        val newReminders = userSettings.reminders
        newReminders.add(
            Reminder(
                event.author.id,
                region,
                words[2].toLong()
            )
        )
        userSettings.reminders = newReminders
        UserSettings.putSettings(userSettings)
        logger.info("Rem: $userSettings")
        event.channel.sendMessage("Your Reminder has been added").complete()
    }

    override fun validate() {
        validateWordCount(3..3)
        validateRegion(1)
        validateNumberPositive(2)
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

        override fun create(event: CommandInfo): DiscordCommand = AddReminderCommand(event)
    }
}

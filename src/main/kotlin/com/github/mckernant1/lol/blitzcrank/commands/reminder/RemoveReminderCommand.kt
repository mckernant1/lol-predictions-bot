package com.github.mckernant1.lol.blitzcrank.commands.reminder

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class RemoveReminderCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        val removed = userSettings.reminders.removeAll {
            it.leagueSlug == region && it.hoursBeforeMatches == words[2].toLong()
        }
        UserSettings.putSettings(userSettings)
        val replyText = if (removed) {
            "Your reminder has been removed"
        } else {
            "None of your reminders matched $region, ${words[2]}"
        }
        reply(replyText)
    }

    override fun validate() {
        validateWordCount(3..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    companion object : CommandMetadata {
        override val commandString: String = "delete-reminders"
        override val commandDescription: String = "Delete a reminder about upcoming games"
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

        override fun create(event: CommandInfo): DiscordCommand = RemoveReminderCommand(event)
    }

}

package com.mckernant1.lol.blitzcrank.commands.pasta

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.commands.reminder.RemoveReminderCommand
import com.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class SetPastaCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {

    override suspend fun execute() {
        userSettings.pasta = event.commandString.replace("/set-Pasta ", "")
        UserSettings.putSettings(userSettings)
        event.channel.sendMessage("Your Pasta has been set to ${userSettings.pasta}").submit().await()
    }

    override suspend fun validate(options: Map<String, String>) {
        if (options["pasta"]!!.length > 100) {
            throw InvalidCommandException("Sorry! Pasta must be less than 100 characters")
        }
    }

    companion object : CommandMetadata {
        override val commandString: String = "set-pasta"
        override val commandDescription: String = "Set your pasta"
        override val commandData: CommandData = commandDataFromJson(
            """
            {
              "name": "$commandString",
              "type": 1,
              "description": "$commandDescription",
              "options": [
                {
                  "name": "pasta",
                  "description": "The pasta you want to save",
                  "type": 3,
                  "required": true
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand =
            SetPastaCommand(event, userSettings)
    }
}

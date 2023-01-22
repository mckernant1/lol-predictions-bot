package com.github.mckernant1.lol.blitzcrank.commands.pasta

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class SetPastaCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        userSettings.pasta = event.commandString.replace("/set-Pasta ", "")
        UserSettings.putSettings(userSettings)
        event.channel.sendMessage("Your Pasta has been set to ${userSettings.pasta}").complete()
    }

    override fun validate() {
        validateWordCount(2..100)
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

        override fun create(event: CommandInfo): DiscordCommand = SetPastaCommand(event)
    }
}

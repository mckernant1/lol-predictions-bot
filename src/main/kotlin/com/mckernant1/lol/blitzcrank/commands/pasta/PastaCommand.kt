package com.mckernant1.lol.blitzcrank.commands.pasta

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class PastaCommand(event: CommandInfo) : DiscordCommand(event) {
    override fun execute() {
        val messageToSend = userSettings.pasta
        if (messageToSend.length >= 2000) {
            event.channel.sendMessage("Your pasta must be less than 2000 characters").complete()
        } else {
            event.channel.sendMessage(messageToSend).complete()
        }
    }

    override fun validate(options: Map<String, String>) {

    }

    companion object : CommandMetadata {
        override val commandString: String = "pasta"
        override val commandDescription: String = "Pastes your pasta"
        override val commandData: CommandData = commandDataFromJson("""
            {
              "name": "$commandString",
              "type": 1,
              "description": "$commandDescription"
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = PastaCommand(event)
    }
}

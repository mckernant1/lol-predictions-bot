package com.mckernant1.lol.blitzcrank.commands.info

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.core.commandsByType
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class InfoCommand(event: CommandInfo) : DiscordCommand(event) {

    override fun execute() {
        val message = infoSubCommands[event.options["help"]] ?: baseInfoMessage
        event.channel.sendMessageEmbeds(message).complete()
    }

    override fun validate(options: Map<String, String>) = Unit

    companion object : CommandMetadata {
        override val commandString: String = "info"
        override val commandDescription: String = "Get the bot info"

        override val commandData: CommandData = commandDataFromJson(
            """
            {
              "name": "$commandString",
              "type": 1,
              "description": "$commandDescription",
              "options": [
                {
                  "name": "help",
                  "description": "The more help",
                  "type": 3,
                  "required": false,
                  "choices": [
                  ${
                commandsByType.keys.joinToString(",") {
                    """{
                      "name": "$it",
                      "value": "$it"
                    }"""
                }
            }
                  ]
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = InfoCommand(event)
    }
}

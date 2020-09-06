package com.github.mckernant1.runner.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        if (!event.author.isBot) {
            event.channel.sendMessage(
                """
                !info -> lists this menu
                ### Esports Commands
                !schedule <league> [number of matches] -> league is required. matches default is 4
                !results <league> [number of matches] -> league is required. matches default is 4
                !standings <league> -> league is required
                ### Predictions Commands
                !predict <league> [number of matches] -> league is required. matches default is 4. Message lasts for 5 mins
                !report <league> [number of matches] -> league is required. matches default is 4
            """.trimIndent()
            ).queue()
        }
    }

    override fun validate(): Boolean = true

}


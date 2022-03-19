package com.github.mckernant1.lol.blitzcrank.commands.info

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: MessageReceivedEvent) : DiscordCommand(event) {


    override suspend fun execute() {
        val message = when (words.getOrNull(1)) {
            "esports" -> esportsInfoMessage
            "predict" -> predictionsInfoMessage
            "settings" -> userSettingsInfoMessage
            else -> baseInfoMessage
        }
        event.channel.sendMessageEmbeds(message).complete()
    }

    override fun validate() = Unit
}

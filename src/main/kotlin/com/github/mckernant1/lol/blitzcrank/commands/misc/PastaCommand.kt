package com.github.mckernant1.lol.blitzcrank.commands.misc

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PastaCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val numberOfTimes = words.getOrNull(1)?.toIntOrNull() ?: 1
        val messageToSend = (1..numberOfTimes).joinToString("") {
            userSettings.pasta + " "
        }
        event.channel.sendMessage(messageToSend).complete()
    }

    override fun validate(): Boolean = validateWordCount(1..2) && validateNumberPositive(1)
}

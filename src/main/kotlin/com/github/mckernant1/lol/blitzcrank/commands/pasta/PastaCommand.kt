package com.github.mckernant1.lol.blitzcrank.commands.pasta

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PastaCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override fun execute() {
        val numberOfTimes = words.getOrNull(1)?.toIntOrNull() ?: 1
        val messageToSend = (1..numberOfTimes).joinToString("") {
            userSettings.pasta + " "
        }
        if (messageToSend.length >= 2000) {
            event.channel.sendMessage("Your pasta must be less than 2000 characters").complete()
        } else {
            event.channel.sendMessage(messageToSend).complete()
        }
    }

    override fun validate() {
        validateWordCount(1..2)
        validateNumberPositive(1)
    }
}

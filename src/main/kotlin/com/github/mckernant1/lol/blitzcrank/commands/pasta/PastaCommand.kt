package com.github.mckernant1.lol.blitzcrank.commands.pasta

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PastaCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))
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

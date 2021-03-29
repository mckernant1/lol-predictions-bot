package com.github.mckernant1.lol.blitzcrank.commands.misc

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class PastaCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        event.channel.sendMessage(userSettings.pasta).complete()
    }

    override fun validate(): Boolean = true
}

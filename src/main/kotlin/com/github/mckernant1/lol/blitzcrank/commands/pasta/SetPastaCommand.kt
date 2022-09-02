package com.github.mckernant1.lol.blitzcrank.commands.pasta

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class SetPastaCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override fun execute() {
        userSettings.pasta = event.message.contentDisplay.replace("!setPasta ", "")
        UserSettings.putSettings(userSettings)
        event.channel.sendMessage("Your Pasta has been set to ${userSettings.pasta}").complete()
    }

    override fun validate() {
        validateWordCount(2..100)
    }
}

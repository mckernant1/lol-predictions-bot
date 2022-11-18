package com.github.mckernant1.lol.blitzcrank.commands.pasta

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

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
}

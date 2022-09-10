package com.github.mckernant1.lol.blitzcrank.commands.info

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
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

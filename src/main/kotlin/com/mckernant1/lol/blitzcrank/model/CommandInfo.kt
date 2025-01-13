package com.mckernant1.lol.blitzcrank.model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction

data class CommandInfo(
    val channel: MessageChannel,
    val author: User,
    val guild: Guild?,
    val commandString: String,
    val isFromGuild: Boolean,
    val options: Map<String, String>
) {

    constructor(event: SlashCommandInteractionEvent) : this(
        event.channel,
        event.user,
        event.guild,
        event.commandString.replace("\\w+: ".toRegex(), ""),
        event.isFromGuild,
        event.options.associate { it.name to it.asString }
    )

}

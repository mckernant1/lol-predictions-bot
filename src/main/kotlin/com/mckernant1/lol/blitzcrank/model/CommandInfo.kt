package com.mckernant1.lol.blitzcrank.model

import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

data class CommandInfo(
    val channel: MessageChannel,
    val author: User,
    val guild: Guild?,
    val commandString: String,
    val isFromGuild: Boolean,
) {

    constructor(event: MessageReceivedEvent) : this(
        event.channel,
        event.author,
        try {
            event.guild
        } catch (e: IllegalStateException) {
            null
        },
        event.message.contentRaw,
        event.isFromGuild
    )

    constructor(event: SlashCommandEvent) : this(
        event.channel,
        event.user,
        event.guild,
        event.commandString.replace("\\w+: ".toRegex(), ""),
        event.isFromGuild
    )

}

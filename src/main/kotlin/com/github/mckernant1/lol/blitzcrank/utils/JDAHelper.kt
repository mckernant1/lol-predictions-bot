package com.github.mckernant1.lol.blitzcrank.utils

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun reactInternalError(m: Message) = m.addReaction("❌").queue()

fun reactUserError(m: Message) = m.addReaction("⛔").queue()

fun reactUserOk(m: Message) = m.addReaction("\uD83D\uDC4C").queue()

fun getServerIdOrUserId(event: MessageReceivedEvent): String = try {
    event.guild.id
} catch (e: IllegalStateException) {
    event.author.id
}

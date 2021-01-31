package com.github.mckernant1.lol.blitzcrank.utils

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun reactInternalError(m: Message): Void = m.addReaction("❌").complete()

fun reactUserError(m: Message): Void = m.addReaction("⛔").complete()

fun reactUserOk(m: Message): Void = m.addReaction("\uD83D\uDC4C").complete()

fun getServerIdOrUserId(event: MessageReceivedEvent): String = try {
    event.guild.id
} catch (e: IllegalStateException) {
    event.author.id
}

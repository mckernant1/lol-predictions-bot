package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import net.dv8tion.jda.api.entities.Message

fun reactInternalError(m: Message): Unit = m.addReaction("❌").queue()

fun reactUserError(m: Message): Unit = m.addReaction("⛔").queue()

fun reactUserOk(m: Message): Unit = m.addReaction("\uD83D\uDC4C").queue()

fun getServerIdOrUserId(event: CommandInfo): String = event.guild?.id ?: event.author.id

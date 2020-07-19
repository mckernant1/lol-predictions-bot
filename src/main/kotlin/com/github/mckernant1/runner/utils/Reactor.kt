package com.github.mckernant1.runner.utils

import net.dv8tion.jda.api.entities.Message

fun reactInternalError(m: Message): Void? = m.addReaction("❌").complete()

fun reactUserError(m: Message): Void? = m.addReaction("⛔").complete()

fun reactUserOk(m: Message): Void? = m.addReaction("\uD83D\uDC4C").complete()

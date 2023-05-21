package com.mckernant1.lol.blitzcrank.utils

import com.mckernant1.lol.blitzcrank.model.CommandInfo

fun getServerIdOrUserId(event: CommandInfo): String = event.guild?.id ?: event.author.id

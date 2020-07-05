package com.github.mckernant1.runner

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun printHelp(event: MessageReceivedEvent) {
    event.channel.sendMessage("""
        !info -> lists this menu
        !schedule <league> [number of matches] -> league is required. matches default is 3
        !results <league> [number of matches] -> league is required. matches default is 3
        !standings <league> -> league is required
    """.trimIndent()).complete()
}

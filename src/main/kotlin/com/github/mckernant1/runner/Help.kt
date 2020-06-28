package com.github.mckernant1.runner

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun printHelp(event: MessageReceivedEvent) {
    event.channel.sendMessage("""
        !help -> lists this menu
        !schedule <league> [number of days] -> get the schedule for a given league. Ex: !schedule lcs 10 gets next 10 matches in lcs
    """.trimIndent())
}

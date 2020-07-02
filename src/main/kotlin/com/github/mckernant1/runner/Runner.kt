package com.github.mckernant1.runner

import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


fun main() {
    JDABuilder.createDefault(BOT_TOKEN)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}


class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = event.message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

        when (words[0]) {
            "!schedule" -> scheduleCmd(words, event)
            "!info" -> printHelp(event)
            "!results" -> resultsCMD(words, event)
//            "!predict" -> predictCmd(words, event)
        }
    }

    override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
        super.onGenericMessageReaction(event)
    }
}

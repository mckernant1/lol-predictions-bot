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
        val words = getWordsFromMessage(event.message)

        when (words[0]) {
            "!schedule" -> scheduleCmd(event)
            "!info" -> printHelp(event)
            "!results" -> resultsCMD(event)
            "!standings" -> standingsCMD(event)
//            "!predict" -> predictCmd(words, event)
        }
    }

    override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
        super.onGenericMessageReaction(event)
    }
}

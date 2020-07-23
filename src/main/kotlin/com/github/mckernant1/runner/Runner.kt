package com.github.mckernant1.runner

import com.github.mckernant1.runner.commands.printHelp
import com.github.mckernant1.runner.commands.resultsCMD
import com.github.mckernant1.runner.commands.scheduleCmd
import com.github.mckernant1.runner.commands.standingsCMD
import com.github.mckernant1.runner.utils.BOT_TOKEN
import com.github.mckernant1.runner.utils.getWordsFromMessage
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
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
        event.channel.sendTyping()
            .queue {
                when (words[0]) {
                    "!schedule" -> scheduleCmd(event)
                    "!info" -> printHelp(event)
                    "!results" -> resultsCMD(event)
                    "!standings" -> standingsCMD(event)
                }
            }
    }
}

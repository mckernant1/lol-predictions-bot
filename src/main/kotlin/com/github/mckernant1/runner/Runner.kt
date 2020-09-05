package com.github.mckernant1.runner

import com.github.mckernant1.runner.commands.*
import com.github.mckernant1.runner.utils.BOT_TOKEN
import com.github.mckernant1.runner.utils.getWordsFromMessage
import com.github.mckernant1.runner.utils.reactUserError
import com.github.mckernant1.runner.utils.reactUserOk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        val cmdMap = mapOf(
            "!schedule" to ScheduleCommand(event),
            "!info" to HelpCommand(event),
            "!results" to ResultsCommand(event),
            "!standings" to StandingsCommand(event),
            "!predict" to PredictCommand(event)
        )


        if (! cmdMap.containsKey(words[0])) {
            return
        }

        event.channel.sendTyping()
            .queue {
                val command = cmdMap[words[0]] ?: return@queue
                if (command.validate()) {
                    reactUserOk(event.message)
                    GlobalScope.launch {
                        command.execute()
                    }
                } else {
                    reactUserError(event.message)
                }
            }
    }
}

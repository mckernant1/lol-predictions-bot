package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.lol.blitzcrank.utils.*
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        thread {
            val words = getWordsFromMessage(event.message)
            if (words.isEmpty() || event.author.id == event.jda.selfUser.id) return@thread

            val command = getCommandFromWords(words, event)
                ?: return@thread

            event.channel.sendTyping().complete()
            runCatching {
                if (command.validate()) {
                    commandValidMetricsAndLogging(words, event)
                    runBlocking {
                        command.execute()
                    }
                } else {
                    reactUserError(event.message)
                    event.channel.sendMessage("Your input was invalid. Please check `!info` to see proper command formatting").complete()
                }
            }.onFailure {
                logger.error("Caught exception while running command '$words': ", it)
                cwp.putErrorMetric()
                reactInternalError(event.message)
                event.channel.sendMessage(createErrorMessage(it)).complete()
            }.onSuccess {
                cwp.putNoErrorMetric()
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)
    }
}

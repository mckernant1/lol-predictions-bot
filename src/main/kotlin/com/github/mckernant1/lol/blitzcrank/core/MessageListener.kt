package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.reactInternalError
import com.github.mckernant1.lol.blitzcrank.utils.reactUserError
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

            try {
                command.validate()
            } catch (e: InvalidCommandException) {
                reactUserError(event.message)
                event.channel.sendMessage("There was an error validating your command:\n${e.message}").complete()
                return@thread
            }

            runCatching {
                commandValidMetricsAndLogging(words, event)
                runBlocking {
                    command.execute()
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

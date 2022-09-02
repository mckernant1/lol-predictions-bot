package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.extensions.strings.capitalize
import com.github.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.reactInternalError
import com.github.mckernant1.lol.blitzcrank.utils.reactUserError
import com.github.mckernant1.standalone.measureDuration
import kotlin.concurrent.thread
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        thread {
            val words = getWordsFromMessage(event.message)
            if (words.isEmpty() || event.author.id == event.jda.selfUser.id) return@thread

            val command = getCommandFromWords(words, event)
                ?: return@thread

            event.channel.sendTyping().complete()
            val commandString ="${words[0].removePrefix("!").capitalize()}Command"

            try {
                val validationDuration = measureDuration {
                    command.validate()
                }
                logger.info("Validation step for $commandString took ${validationDuration.toMillis()}ms")
            } catch (e: InvalidCommandException) {
                reactUserError(event.message)
                event.channel.sendMessage("There was an error validating your command:\n${e.message}").complete()
                return@thread
            } catch (e: Exception) {
                logger.error("Caught exception while running command '$words': ", e)
                cwp.putErrorMetric()
                event.channel.sendMessageEmbeds(createErrorMessage(e)).complete()
                return@thread
            }

            runCatching {
                commandValidMetricsAndLogging(words, event)
                val executeDuration = measureDuration {
                    command.execute()
                }
                logger.info("Execution step for $commandString took ${executeDuration.toMillis()}ms")
            }.onFailure {
                logger.error("Caught exception while running command '$words': ", it)
                cwp.putErrorMetric()
                reactInternalError(event.message)
                event.channel.sendMessageEmbeds(createErrorMessage(it)).complete()
            }.onSuccess {
                cwp.putNoErrorMetric()
            }
        }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)
    }
}

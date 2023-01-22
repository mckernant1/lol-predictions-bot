package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.extensions.strings.capitalize
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromString
import com.github.mckernant1.standalone.measureDuration
import kotlin.concurrent.thread
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class MessageListener : ListenerAdapter() {

    private val threadPool = Executors.newFixedThreadPool(10)

    override fun onSlashCommand(slashEvent: SlashCommandEvent) {
        threadPool.submit {
            val event = CommandInfo(slashEvent)
            val words = getWordsFromString(event.commandString)
            logger.info("Got slash command ${event.commandString}")
            logger.info("Got params ${slashEvent.options}")
            val command = getCommandFromWords(words, event)
                ?: run {
                    slashEvent.reply("Could not find this command").complete()
                    return@submit
                }
            val hook = slashEvent.reply("Command Received!").complete()

            handleCommonCommandLogic(event, command, words)

            try {
                hook.deleteOriginal().complete()
            } catch (e: Exception) {
                logger.warn("Failed to delete original message", e)
            }
        }
    }

    override fun onMessageReceived(messageEvent: MessageReceivedEvent) {
        thread {
            val words = getWordsFromMessage(messageEvent.message)
            if (words.isEmpty() || messageEvent.author.id == messageEvent.jda.selfUser.id) return@thread
            val event = CommandInfo(messageEvent)

            val command = getCommandFromWords(words, event)
                ?: return@thread

            messageEvent.channel.sendMessage(
                """
This bot is migrating to discord slash commands. Please type / to activate them. 
Running commands with `!` will become unavailable on Oct 1st 2022.
                
You may have to re-add the bot your server to allow this.
Feel free to join the support discord with any questions https://discord.gg/cHRU5jcFc6
            """.trimMargin()
            ).complete()
            handleCommonCommandLogic(event, command, words)
        }
    }

    private fun handleCommonCommandLogic(
        event: CommandInfo,
        command: DiscordCommand,
        words: List<String>
    ) {
        val commandString = "${words[0].removePrefix("!").capitalize()}Command"

        try {
            val validationDuration = measureDuration {
                command.validate()
            }
            logger.info("Validation step for $commandString took ${validationDuration.toMillis()}ms")
        } catch (e: InvalidCommandException) {
            event.channel.sendMessage("There was an error validating your command:\n${e.message}").complete()
            return
        } catch (e: Exception) {
            logger.error(
                "Caught exception while validating command for user: ${event.author.id}, commands: '$words': ",
                e
            )
            cwp.putErrorMetric()
            event.channel.sendMessageEmbeds(createErrorMessage(e)).complete()
            return
        }

        try {
            commandValidMetricsAndLogging(words, event)
            val executeDuration = measureDuration {
                command.execute()
            }
            logger.info("Execution step for $commandString took ${executeDuration.toMillis()}ms")
        } catch (e: InsufficientPermissionException) {
            logger.warn("Hit insufficient permissions for server: '${event.guild?.id}'", e)
            return
        } catch (e: Exception) {
            logger.error(
                "Caught exception while executing command for user: ${event.author.id}, commands: '$words': ",
                e
            )
            cwp.putErrorMetric()
            event.channel.sendMessageEmbeds(createErrorMessage(e)).complete()
            return
        }
        cwp.putNoErrorMetric()
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)
    }
}

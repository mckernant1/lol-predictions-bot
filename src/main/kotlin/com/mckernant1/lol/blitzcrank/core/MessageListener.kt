package com.mckernant1.lol.blitzcrank.core

import com.mckernant1.commons.extensions.strings.capitalize
import com.mckernant1.commons.logging.Slf4j.logger
import com.mckernant1.commons.standalone.measureDuration
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandThreadPool
import com.mckernant1.lol.blitzcrank.utils.cwp
import com.mckernant1.lol.blitzcrank.utils.getWordsFromString
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.slf4j.Logger

class MessageListener : ListenerAdapter() {

    private val metrics = cwp.newMetrics("ErrorCount" to "Error")

    override fun onSlashCommandInteraction(slashEvent: SlashCommandInteractionEvent) {
        commandThreadPool.submit {

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

    private fun handleCommonCommandLogic(
        event: CommandInfo,
        command: DiscordCommand,
        words: List<String>,
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
            metrics.withNewMetrics { m ->
                m.addCount("Error", 1)
            }
            event.channel.sendMessageEmbeds(createErrorMessage(e)).complete()
            return
        }

        try {
            commandValidMetricsAndLogging(words, event)
            val executeDuration = measureDuration {
                command.execute()
            }
            logger.info("Execution step for $commandString took ${executeDuration.toMillis()}ms")
            metrics.withNewMetrics { m ->
                m.addCount("Error", 0)
            }
        } catch (e: InsufficientPermissionException) {
            logger.warn("Hit insufficient permissions for server: '${event.guild?.id}'", e)
        } catch (e: ErrorResponseException) {
            when (e.errorCode) {
                10008 -> {
                    logger.warn("We tried to react to a message that didn't exist", e)
                }

                10003 -> {
                    logger.warn("The channel we tried to post to a channel that doesnt exist", e)
                }

                50001 -> {
                    logger.warn("Hit Permission issue. Known to be an issue with reaction permissions", e)
                    event.channel.sendMessage("The Bot has encountered a permission issue. From what I know this is an issue with reaction permissions")
                        .complete()
                }

                50013 -> {
                    logger.warn("Hit Permission issue. Known to be an issue with embedded message permissions ", e)
                    event.channel.sendMessage("The Bot has encountered a permission issue. From what I know this is an issue with embedded message permissions")
                        .complete()
                }

                else -> defaultErrBehavior(event, words, e)
            }
        } catch (e: Exception) {
            defaultErrBehavior(event, words, e)
        }
    }

    private fun defaultErrBehavior(event: CommandInfo, words: List<String>, e: Exception) {
        logger.error(
            "Caught exception while executing command for user: ${event.author.id}, guild: ${event.guild?.id}, commands: '$words': ",
            e
        )
        metrics.withNewMetrics { m ->
            m.addCount("Error", 1)
        }
        event.channel.sendMessageEmbeds(createErrorMessage(e)).complete()
    }

    companion object {
        private val logger: Logger = logger()
    }
}

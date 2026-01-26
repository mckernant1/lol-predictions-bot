package com.mckernant1.lol.blitzcrank.core

import com.mckernant1.commons.extensions.strings.capitalize
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.cwp
import com.mckernant1.lol.blitzcrank.utils.getServerIdOrUserId
import net.dv8tion.jda.api.EmbedBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val logger: Logger = LoggerFactory.getLogger("RunCommandLogger")
suspend fun getCommandFromWords(words: List<String>, event: CommandInfo): DiscordCommand? =
    commandsByName[words[0].drop(1)]?.create(
        event,
        UserSettings.getSettingsForUser(event.author.id)
    )

private val metrics by lazy {
    cwp.newMetrics(
        "Commands" to "Count"
    )
}

suspend fun commandValidMetricsAndLogging(words: List<String>, event: CommandInfo) {
    val commandString = "${words[0].drop(1).capitalize()}Command"
    val serverOrUser = if (event.isFromGuild) "server" else "user"
    logger.info(
        "Running command='${commandString}' with arguments='$words' from='${serverOrUser}' with id='${
            getServerIdOrUserId(
                event
            )
        }'"
    )
    metrics.withNewMetrics {
        it.addCount(commandString, 1)
    }
}

fun createErrorMessage(e: Throwable) = EmbedBuilder()
    .addField("Whoops!", "We have an encountered an error, you can file a github issue if you feel like it", true)
    .addField(
        "For the Developers....",
        "Timestamp: ${ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)}\n" +
                "Error message: ${e.message}", false
    )
    .addField(
        "Useful links",
        "[Create a Github Issue](https://github.com/mckernant1/lol-predictions-bot/issues/new)\n[Join Discord Support Server](https://discord.gg/Dvq8f5KxZT)",
        false
    )
    .build()

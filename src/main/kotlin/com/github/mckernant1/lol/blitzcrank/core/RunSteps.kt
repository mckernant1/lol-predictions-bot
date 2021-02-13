package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.lol.blitzcrank.commands.lol.*
import com.github.mckernant1.lol.blitzcrank.commands.util.InfoCommand
import com.github.mckernant1.lol.blitzcrank.commands.util.SetTimezoneCommand
import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.getServerIdOrUserId
import com.github.mckernant1.lol.blitzcrank.utils.reactUserOk
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


private val logger: Logger = LoggerFactory.getLogger("RunCommandLogger")

fun getCommandFromWords(words: List<String>, event: MessageReceivedEvent) = when (words[0]) {
    "!schedule" -> ScheduleCommand(event)
    "!info" -> InfoCommand(event)
    "!results" -> ResultsCommand(event)
    "!standings" -> StandingsCommand(event)
    "!predict" -> PredictCommand(event)
    "!report" -> ReportCommand(event, true)
    "!predictions" -> ReportCommand(event, false)
    "!stats" -> StatsCommand(event)
    "!roster" -> RosterCommand(event)
    "!setTimezone" -> SetTimezoneCommand(event)
    "!record" -> RecordCommand(event)
    else -> null
}

fun commandValidMetricsAndLogging(words: List<String>, event: MessageReceivedEvent) {
    reactUserOk(event.message)
    val commandString = "${words[0].removePrefix("!").capitalize()}Command"
    val serverOrUser = if (event.isFromGuild) "server" else "user"
    logger.info(
        "Running command='${commandString}' from='${serverOrUser}' with id='${
            getServerIdOrUserId(
                event
            )
        }'"
    )
    cwp.putCommandUsedMetric(commandString)
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

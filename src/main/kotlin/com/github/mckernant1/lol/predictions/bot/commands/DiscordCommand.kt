package com.github.mckernant1.lol.predictions.bot.commands

import com.github.mckernant1.lol.predictions.bot.utils.getLeagues
import com.github.mckernant1.lol.predictions.bot.utils.getWordsFromMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

abstract class DiscordCommand(protected val event: MessageReceivedEvent) {

    protected lateinit var region: String

    protected var numToGet: Int? = null

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val dateFormat: DateTimeFormatter = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.LONG)
        .withZone(ZoneId.of("America/Los_Angeles"))


    abstract suspend fun execute()

    abstract fun validate(): Boolean

    protected fun validateNumberOfMatches(event: MessageReceivedEvent, position: Int): Boolean {
        val words = getWordsFromMessage(event.message)
        numToGet = words.getOrNull(position)?.toInt()
        return (numToGet == null || numToGet!! >= 1)
            .also { logger.info("validateNumberOfMatches with number $numToGet and result: $it") }
    }

    protected fun validateRegion(event: MessageReceivedEvent, position: Int): Boolean {
        region = getWordsFromMessage(event.message)[position]
        return (getLeagues().find { it.name.equals(region, ignoreCase = true) } != null)
            .also { logger.info("validateRegion with region: $region and result: $it") }
    }

    protected fun validateWordCount(event: MessageReceivedEvent, range: IntRange): Boolean {
        return getWordsFromMessage(event.message).size in range
    }

}

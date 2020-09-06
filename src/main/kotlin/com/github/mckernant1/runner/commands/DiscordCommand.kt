package com.github.mckernant1.runner.commands

import com.github.mckernant1.runner.utils.getLeagues
import com.github.mckernant1.runner.utils.getWordsFromMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.DateFormat
import java.time.ZoneId
import java.util.*

abstract class DiscordCommand(protected val event: MessageReceivedEvent) {

    protected lateinit var region: String

    protected var numToGet: Int = 0

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val dateFormat: DateFormat = DateFormat.getDateTimeInstance(
        DateFormat.FULL, DateFormat.LONG
    ).apply { timeZone = TimeZone.getTimeZone(ZoneId.of("America/Los_Angeles")) }


    abstract suspend fun execute()

    abstract fun validate(): Boolean


    init {
        logger.info("logger started")
    }

    protected fun validateNumberOfMatches(event: MessageReceivedEvent, position: Int, default: Int = 4): Boolean {
        val words = getWordsFromMessage(event.message)
        numToGet = words.getOrNull(position)?.toInt() ?: default
        return (numToGet in 1..19)
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

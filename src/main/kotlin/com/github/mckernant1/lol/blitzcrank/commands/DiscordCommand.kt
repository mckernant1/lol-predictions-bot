package com.github.mckernant1.lol.blitzcrank.commands

import com.github.mckernant1.lol.blitzcrank.utils.getLeagues
import com.github.mckernant1.lol.blitzcrank.utils.getTeams
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.userSettingsTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

abstract class DiscordCommand(protected val event: MessageReceivedEvent) {

    protected lateinit var region: String
    protected var numToGet: Int? = null
    protected val words = getWordsFromMessage(event.message)

    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    protected val userSettings by lazy {
        userSettingsTable.getSettingsForUser(event.author.id)
    }

    protected val longDateFormat: DateTimeFormatter by lazy {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.LONG)
            .withZone(
                ZoneId.of(userSettings.timezone)
            )
    }

    protected val shortDateFormat: DateTimeFormatter by lazy {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT)
            .withZone(
                ZoneId.of(userSettings.timezone)
            )
    }

    abstract suspend fun execute()

    abstract fun validate(): Boolean

    protected fun validateNumberOfMatches(position: Int): Boolean {
        numToGet = try {
            words.getOrNull(position)?.toInt()
        } catch (e: NumberFormatException) {
            return false
        }
        return (numToGet == null || numToGet!! >= 1)
            .also { logger.info("validateNumberOfMatches with number $numToGet and result: $it") }
    }

    protected fun validateRegion(position: Int): Boolean {
        region = words[position]
        return (getLeagues().find { it.name.equals(region, ignoreCase = true) } != null)
            .also { logger.info("validateRegion with region: ${region.toUpperCase()} and result: $it") }
    }

    protected fun validateTeam(position: Int): Boolean {
        val teamName = words[position]
        return getTeams().any { it.code.equals(teamName, ignoreCase = true) }
            .also { logger.info("validateTeam returned result $it") }
    }

    protected fun validateWordCount(range: IntRange): Boolean {
        return (words.size in range)
            .also { logger.info("validateWordCount returned result $it ") }
    }

}

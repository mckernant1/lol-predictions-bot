package com.github.mckernant1.lol.blitzcrank.commands

import com.github.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.github.mckernant1.lol.blitzcrank.exceptions.LeagueDoesNotExistException
import com.github.mckernant1.lol.blitzcrank.exceptions.TeamDoesNotExistException
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.lol.blitzcrank.utils.getLeagues
import com.github.mckernant1.lol.blitzcrank.utils.getTeams
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
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

    protected val logger: Logger = LoggerFactory.getLogger("${event.author.id}-${words}")

    protected val userSettings by lazy {
        UserSettings.getSettingsForUser(event.author.id)
    }

    protected val longDateFormat: DateTimeFormatter by lazy {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.LONG)
            .withZone(
                ZoneId.of(userSettings.timezone)
            )
    }

    protected val mediumDateFormat: DateTimeFormatter by lazy {
        DateTimeFormatter
            .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM)
            .withZone(
                ZoneId.of(userSettings.timezone)
            )
    }

    abstract suspend fun execute(): Unit

    @Throws(InvalidCommandException::class)
    abstract fun validate(): Unit

    protected fun validateNumberPositive(position: Int) {
        numToGet = try {
            words.getOrNull(position)?.toInt()
        } catch (e: NumberFormatException) {
            throw InvalidCommandException("Input must be a number")
        }

        if (numToGet == null || numToGet!! >= 1) {
            logger.info("validateNumberOfMatches with number $numToGet")
        } else {
            throw InvalidCommandException("Number cannot be negative")
        }
    }

    protected fun validateRegion(position: Int) {
        region = words[position]
        if (getLeagues().find { it.slug.equals(region, ignoreCase = true) } != null) {
            logger.info("validateRegion with region: '${region.toUpperCase()}'")
        } else {
            throw LeagueDoesNotExistException("League '$region' does not exist. Available regions: ${
                getLeagues().joinToString(", ") { it.slug.capitalize() }
            }")
        }

    }

    protected fun validateTeam(position: Int) {
        val teamName = words[position]
        if (getTeams().any { it.code.equals(teamName, ignoreCase = true) }) {
            logger.info("Team '$teamName' has been selected")
        } else {
            throw TeamDoesNotExistException("Team '$teamName' does not exists")
        }

    }

    protected fun validateWordCount(range: IntRange) {
        if (words.size !in range) {
            throw InvalidCommandException("Invalid word count. There should be between $range words, but there are '${words.size}'")
        }
    }

    protected fun reply(message: String) = event.channel.sendMessage(message).complete()
}

package com.mckernant1.lol.blitzcrank.commands

import com.mckernant1.commons.standalone.measureOperation
import com.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.mckernant1.lol.blitzcrank.exceptions.LeagueDoesNotExistException
import com.mckernant1.lol.blitzcrank.exceptions.TeamDoesNotExistException
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.getWordsFromString
import com.mckernant1.lol.blitzcrank.utils.model.BotUser
import net.dv8tion.jda.api.entities.Message
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

abstract class DiscordCommand(protected val event: CommandInfo) {

    companion object {
        private val LEAGUE_MAP = mapOf<String, String>(
            "lcs" to "lta_n",
            "cblol" to "lta_s",
            "worlds" to "wcs"
        )
    }

    protected lateinit var region: String
    protected var numToGet: Int? = null

    protected val logger: Logger = LoggerFactory.getLogger("${event.author.id}-${event.commandString}")

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

    abstract fun execute(): Unit

    @Throws(InvalidCommandException::class)
    abstract fun validate(options: Map<String, String>): Unit

    protected fun validateNumberPositive(input: String?) {
        numToGet = try {
            input?.toInt()
        } catch (e: NumberFormatException) {
            throw InvalidCommandException("Input must be a number")
        }

        if (numToGet == null || numToGet!! >= 1) {
            logger.info("validateNumberOfMatches with number $numToGet")
        } else {
            throw InvalidCommandException("Number cannot be negative")
        }
    }

    protected fun validateRegion(leagueId: String?) {
        region = leagueId
            ?: throw InvalidCommandException("league_id cannot be null")

        if (leagueId in LEAGUE_MAP) {
            region = LEAGUE_MAP[leagueId]!!
        }

        try {
            apiClient.getLeagueByCode(region.uppercase())
            logger.info("validateRegion with region: '${region.uppercase()}'")
        } catch (e: Exception) {
            throw LeagueDoesNotExistException("League '$region' does not exist. Available regions: ${
                apiClient.leagues.joinToString(", ") { it.leagueId.uppercase() }
            }")
        }

    }

    protected fun validateTeam(teamName: String?) {
        teamName ?: throw InvalidCommandException("Team name cannot be null")
        try  {
            apiClient.getTeamByCode(teamName.uppercase())
            logger.info("Team '$teamName' has been selected")
        } catch(e: Exception) {
            throw TeamDoesNotExistException("Team '$teamName' does not exists. Use team code, not team name (C9 not Cloud9)")
        }

    }

    protected fun getAllUsersForServer(): List<BotUser> {
        return if (event.isFromGuild) {
            val (duration, users) = measureOperation {
                event.guild!!.loadMembers()
                    .get()
                    .map { BotUser(it, UserSettings.getSettingsForUser(it.id)) }
            }
            logger.info("loading ${users.size} members took ${duration.toMillis()}ms")
            users
        } else {
            listOf(event.author).map { BotUser(it, UserSettings.getSettingsForUser(it.id)) }
        }
    }

    protected fun reply(message: String): Message = event.channel.sendMessage(message).complete()
}

package com.mckernant1.lol.blitzcrank.timers

import com.mckernant1.commons.extensions.executor.Executors.scheduleAtFixedRate
import com.mckernant1.commons.extensions.time.Instants.timeUntilNextWhole
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.periodicActionsThreadPool
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.streams.asSequence

private val logger by lazy {
    LoggerFactory.getLogger("ReminderChecker")
}

fun reminderChecker(bot: JDA) {
    periodicActionsThreadPool.scheduleAtFixedRate(
        Duration.ofMinutes(30).toMillis(),
        Instant.now().timeUntilNextWhole(ChronoUnit.HOURS).toMillis(),
        TimeUnit.MILLISECONDS
    ) {
        try {
            logger.info("Starting Reminder Checker")
            UserSettings.scan().asSequence()
                .flatMap { it.reminders }
                .filter { it.shouldSendMessage() }
                .onEach {
                    try {
                        val user = bot.getUserById(it.userId) ?: return@onEach
                        val privateChannel = user.openPrivateChannel().complete() ?: return@onEach
                        privateChannel.sendMessage(
                            "Reminder: ${it.leagueSlug} is coming up in ${it.hoursBeforeMatches} hours"
                        ).complete()
                    } catch (e: ErrorResponseException) {
                        val knownErrorResponse = when (e.errorCode) {
                            50001 -> "Bot does not have permission to channel it was called from"
                            50007 -> "User '${it.userId}' does not have PMs enabled"
                            else -> null
                        }
                        if (knownErrorResponse != null) {
                            logger.warn("This is an exception from discord API. Details: $knownErrorResponse, Code: ${e.errorCode}, Message: ${e.meaning}")
                        } else {
                            logger.error("This is an exception from discord API. We have not seen this code before", e)
                        }
                    } catch (e: Exception) {
                        logger.error("An unknown error occurred while sending reminders", e)
                    }
                }.groupBy { it.userId }
                .forEach { (userId, reminders) ->
                    try {
                        val user = UserSettings.getSettingsForUser(userId)
                        val newReminders = reminders.map {
                            it.lastReminderSentEpochMillis = Instant.now().toEpochMilli()
                            it
                        }.toMutableList()
                        user.reminders = newReminders
                        UserSettings.putSettings(user)
                    } catch (e: Exception) {
                        logger.error("Failed to update user reminder for userId: '{}'", userId, e)
                    }
                }
        } catch (e: Exception) {
            logger.error("Exception happened during reminders: ", e)
        }
    }
}

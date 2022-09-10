package com.github.mckernant1.lol.blitzcrank.timers

import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.extensions.executor.scheduleAtFixedRate
import com.github.mckernant1.extensions.time.timeUntilNextWhole
import com.github.mckernant1.lol.blitzcrank.utils.globalThreadPool
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
    globalThreadPool.scheduleAtFixedRate(
        Duration.ofMinutes(30).toMillis(),
        Instant.now().timeUntilNextWhole(ChronoUnit.HOURS).toMillis(),
        TimeUnit.MILLISECONDS
    ) {
        runCatching {
            UserSettings.scan().asSequence().flatMap { it.reminders }
                .filter { it.shouldSendMessage() }
                .onEach {
                    val user = bot.getUserById(it.userId) ?: return@onEach
                    val privateChannel = user.openPrivateChannel().complete() ?: return@onEach
                    privateChannel.sendMessage(
                        "Reminder: ${it.leagueSlug} is coming up in ${it.hoursBeforeMatches} hours"
                    ).complete()

                }.groupBy { it.userId }
                .forEach { (userId, reminders) ->
                    val user = UserSettings.getSettingsForUser(userId)
                    val newReminders = reminders.map {
                        it.lastReminderSentEpochMillis = Instant.now().toEpochMilli()
                        it
                    }.toMutableList()
                    user.reminders = newReminders
                    UserSettings.putSettings(user)
                }
        }.onFailure {
            when (it) {
                is ErrorResponseException -> {
                    val knownErrorCodes = when (it.errorCode) {
                        50001 -> "Bot does not have permission to channel it was called from"
                        50007 -> "User does not have PMs enabled"
                        else -> "Unknown Code"
                    }
                    logger.warn("This is an ErrorResponseException from discord API. Details: $knownErrorCodes, Error Code: ${it.errorCode}, Message: ${it.meaning}")
                }
                else -> {
                    logger.error("An error occurred while sending reminders. This is not an ErrorResponseException", it)
                }
            }
        }
    }
}

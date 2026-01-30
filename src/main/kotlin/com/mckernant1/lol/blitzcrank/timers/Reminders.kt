package com.mckernant1.lol.blitzcrank.timers

import com.mckernant1.commons.extensions.coroutines.Schedule.scheduleAtFixedRate
import com.mckernant1.commons.extensions.executor.Executors.scheduleAtFixedRate
import com.mckernant1.commons.extensions.time.Instants.timeUntilNextWhole
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.coroutineScope
import com.mckernant1.lol.blitzcrank.utils.periodicActionsThreadPool
import kotlinx.coroutines.future.await
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

private val logger by lazy {
    LoggerFactory.getLogger("ReminderChecker")
}


suspend fun reminderCheckerWork(
    bot: JDA,
    userSettings: UserSettings,
) {
    logger.info("Checking reminders for user ${userSettings.discordId}")
    val user = bot.getUserById(userSettings.discordId!!) ?: return
    val privateChannel = user.openPrivateChannel().submit().await() ?: return
    val reminders = userSettings.reminders

    for (it in reminders) {
        if (!it.shouldSendMessage()) continue
        try {
            privateChannel.sendMessage(
                "Reminder: ${it.leagueSlug} is coming up in ${it.hoursBeforeMatches} hours"
            ).submit().await()
            it.lastReminderSentEpochMillis = Instant.now().toEpochMilli()
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
    }
    logger.info("Putting ${userSettings.reminders.size} reminders for user ${userSettings.discordId}")
    userSettings.reminders = reminders
    UserSettings.putSettings(userSettings)
}

fun reminderChecker(bot: JDA) {
    coroutineScope.scheduleAtFixedRate(
        Instant.now().timeUntilNextWhole(ChronoUnit.HOURS),
        Duration.ofMinutes(30)
    ) {
        UserSettings.scan().collect {
            reminderCheckerWork(bot, it)
        }
    }
}

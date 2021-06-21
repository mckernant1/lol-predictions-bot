package com.github.mckernant1.lol.blitzcrank.timers

import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.lol.blitzcrank.utils.globalTimer
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.streams.asSequence

private val logger by lazy {
    LoggerFactory.getLogger("ReminderChecker")
}

fun reminderChecker(bot: JDA) {
    globalTimer.scheduleAtFixedRate(0, Duration.ofMinutes(5).toMillis()) {
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
            logger.error("An error occurred while sending reminders", it)
        }
    }
}

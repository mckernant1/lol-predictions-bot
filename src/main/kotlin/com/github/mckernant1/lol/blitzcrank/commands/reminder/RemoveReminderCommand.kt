package com.github.mckernant1.lol.blitzcrank.commands.reminder

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RemoveReminderCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val removed = userSettings.reminders.removeAll {
            it.leagueSlug == region && it.hoursBeforeMatches == words[2].toLong()
        }
        UserSettings.putSettings(userSettings)
        val replyText = if (removed) {
            "Your reminder has been removed"
        } else {
            "None of your reminders matched $region, ${words[2]}"
        }
        reply(replyText)
    }

    override fun validate() {
        validateWordCount(3..3)
        validateRegion(1)
        validateNumberPositive(2)
    }
}

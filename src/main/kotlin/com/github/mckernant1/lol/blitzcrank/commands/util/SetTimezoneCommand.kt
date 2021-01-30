package com.github.mckernant1.lol.blitzcrank.commands.util

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.userSettingsTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.ZoneId
import java.time.ZonedDateTime

class SetTimezoneCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val userSpecifiedTimezone = getWordsFromMessage(event.message)[1]

        val userZoneIdIsValid: Result<ZoneId> = userSpecifiedTimezone.runCatching {
            ZoneId.of(this)
        }

        val userZoneIdIsCode: Result<ZoneId> = userSpecifiedTimezone.runCatching {
            ZoneId.of(ZoneId.SHORT_IDS[this]!!)
        }

        val zoneId = when {
            userZoneIdIsValid.isSuccess -> userZoneIdIsValid.getOrNull()
            userZoneIdIsCode.isSuccess -> userZoneIdIsCode.getOrNull()
            else -> null
        }

        if (zoneId == null) {
            event.channel.sendMessage("Sorry, we could not interpret the time code '$userSpecifiedTimezone'").complete()
            return
        }

        val userSettings = userSettingsTable.getSettingsForUser(event.author.id)
        userSettings.timezone = zoneId.id
        userSettingsTable.putSettings(userSettings)

        event.channel.sendMessage(
            "Your timezone has now been set to ${zoneId.id}. Your current time should be ${dateFormat.withZone(zoneId).format(ZonedDateTime.now())}"
        ).complete()
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2)
    }
}

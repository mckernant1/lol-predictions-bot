package com.github.mckernant1.lol.blitzcrank.commands.util

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.userSettingsTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.zone.ZoneRulesException

class SetTimezoneCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val getUserSpecifiedTimezone = getWordsFromMessage(event.message)[1]
        val zoneId = ZoneId.of(getUserSpecifiedTimezone)

        val userSettings = userSettingsTable.getSettingsForUser(event.author.id)
        userSettings.timezone = zoneId.id
        userSettingsTable.putSettings(userSettings)

        event.channel.sendMessage(
            "Your timezone has now been set to ${zoneId.id}. Your current time should be ${dateFormat.withZone(zoneId).format(ZonedDateTime.now())}"
        ).complete()
    }

    override fun validate(): Boolean {
        val rightNumberOfArgs = validateWordCount(event, 2..2)
        if (rightNumberOfArgs.not()) return false

        val getUserSpecifiedTimezone = getWordsFromMessage(event.message)[1]
        try {
            ZoneId.of(getUserSpecifiedTimezone)
        } catch (zre: ZoneRulesException) {
            if (ZoneId.SHORT_IDS.containsKey(getUserSpecifiedTimezone)) {
                event.channel.sendMessage(
                    "Sorry. Did you mean: `!setTimezone ${ZoneId.SHORT_IDS[getUserSpecifiedTimezone]}`?"
                ).complete()
            } else {
                event.channel.sendMessage("Sorry. We could not recognize that time code").complete()
            }
            return false
        }
        return true
    }
}

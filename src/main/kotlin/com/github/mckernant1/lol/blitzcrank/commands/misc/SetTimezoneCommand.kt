package com.github.mckernant1.lol.blitzcrank.commands.misc

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.ZoneId
import java.time.ZonedDateTime

class SetTimezoneCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        val userSpecifiedTimezone = words[1]

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

        val userSettings = UserSettings.getSettingsForUser(event.author.id)
        userSettings.timezone = zoneId.id
        UserSettings.putSettings(userSettings)

        event.channel.sendMessage(
            "Your timezone has now been set to ${zoneId.id}. Your current time should be ${
                longDateFormat.withZone(
                    zoneId
                ).format(ZonedDateTime.now())
            }"
        ).complete()
    }

    override fun validate() {
        validateWordCount(2..2)
    }

    companion object : CommandMetadata {
        override val commandString: String = "set-timezone"
        override val commandDescription: String = "Get the roster for the given team"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription",
                  "options": [
                    {
                      "name": "timezone",
                      "description": "The timezone",
                      "type": 3,
                      "required": true
                    }
                  ]
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = SetTimezoneCommand(event)
    }
}

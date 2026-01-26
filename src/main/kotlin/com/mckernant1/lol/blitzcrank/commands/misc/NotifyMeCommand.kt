package com.mckernant1.lol.blitzcrank.commands.misc

import com.mckernant1.commons.extensions.boolean.trueIfNull
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.commands.reminder.RemoveReminderCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class NotifyMeCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {


    override suspend fun validate(options: Map<String, String>) {
    }

    override suspend fun execute() {
        val notify = event.options["notify"]?.toBoolean().trueIfNull()
        val settings = userSettings
        settings.notifyMe = notify
        UserSettings.putSettings(settings)

        val response = if (notify) {
            "You will now be notified by other commands"
        } else {
            "You will no longer be notified by other commands"
        }

        reply(response)
    }

    companion object : CommandMetadata {
        override val commandString: String = "notify-me"
        override val commandDescription: String =
            "Whether or not to notify me when someone uses the commands that list people"
        override val commandData: CommandData = CommandDataImpl(
            commandString, commandDescription
        ).addOption(
            OptionType.BOOLEAN, "notify", "true if you want to be notified", true
        )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand =
            NotifyMeCommand(event, userSettings)
    }
}

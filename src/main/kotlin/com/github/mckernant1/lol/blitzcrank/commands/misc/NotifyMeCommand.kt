package com.github.mckernant1.lol.blitzcrank.commands.misc

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class NotifyMeCommand(event: CommandInfo) : DiscordCommand(event) {


    override fun validate() {
        validateWordCount(2..2)
    }

    override fun execute() {
        val notify = words[1].toBooleanStrictOrNull() ?: true
        val settings = userSettings
        settings.notifyMe = notify
        UserSettings.putSettings(settings)
    }

    companion object : CommandMetadata {
        override val commandString: String = "notify-me"
        override val commandDescription: String =
            "Whether or not to notify me when someone uses the commands that list people"
        override val commandData: CommandData = CommandData(
            commandString, commandDescription
        ).addOption(
            OptionType.BOOLEAN, "notify", "true if you want to be notified", true
        )

        override fun create(event: CommandInfo): DiscordCommand = NotifyMeCommand(event)
    }
}

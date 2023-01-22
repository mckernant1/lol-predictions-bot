package com.github.mckernant1.lol.blitzcrank.commands.misc

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.UserSettings

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


}

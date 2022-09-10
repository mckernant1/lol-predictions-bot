package com.github.mckernant1.lol.blitzcrank.commands.reminder

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.Reminder
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class AddReminderCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))
    override fun execute() {
        val newReminders = userSettings.reminders
        newReminders.add(
            Reminder(
                event.author.id,
                region,
                words[2].toLong()
            )
        )
        userSettings.reminders = newReminders
        UserSettings.putSettings(userSettings)
        logger.info("Rem: $userSettings")
        event.channel.sendMessage("Your Reminder has been added").complete()
    }

    override fun validate() {
        validateWordCount(3..3)
        validateRegion(1)
        validateNumberPositive(2)
    }
}

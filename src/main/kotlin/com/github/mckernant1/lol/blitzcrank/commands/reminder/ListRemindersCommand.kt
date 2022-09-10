package com.github.mckernant1.lol.blitzcrank.commands.reminder

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ListRemindersCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))
    override fun execute() {
        event.channel.sendMessage("""
            Your Reminders
            ${userSettings.reminders.joinToString("-----------------") {
            """
            League: ${it.leagueSlug}
            Hours before: ${it.hoursBeforeMatches}
            """ }
            }
        """.trimIndent()).complete()
    }

    override fun validate() {
       validateWordCount(1..1)
    }
}

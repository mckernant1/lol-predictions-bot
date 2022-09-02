package com.github.mckernant1.lol.blitzcrank.commands.reminder

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ListRemindersCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
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

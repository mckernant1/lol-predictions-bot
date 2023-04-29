package com.mckernant1.lol.blitzcrank.commands.reminder

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

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

    companion object : CommandMetadata {
        override val commandString: String = "list-reminders"
        override val commandDescription: String = "List your reminders"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription"
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = ListRemindersCommand(event)
    }
}

package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class OngoingTournamentsCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        val ongoingTourneys = apiClient.ongoingTournanments
            .map { apiClient.getLeagueByCode(it.leagueId) }
        val ongoingTourneyString = if (ongoingTourneys.isEmpty()) {
            "There are no ongoing tournaments"
        } else {
            ongoingTourneys.joinToString("\n") {
                "- [**${it.leagueId}**] ${it.leagueName}"
            }
        }

        val messageToSend = """
            **Leagues with ongoing tournaments**:
$ongoingTourneyString
        """.trim()

        event.channel.sendMessage(messageToSend).complete()
    }

    override fun validate() {
        validateWordCount(1..1)
    }
}

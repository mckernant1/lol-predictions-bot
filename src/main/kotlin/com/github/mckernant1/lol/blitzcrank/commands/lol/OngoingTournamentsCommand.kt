package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class OngoingTournamentsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val ongoingTourneys = apiClient.ongoingTournanments
            .map { apiClient.getLeagueByCode(it.leagueId) }
            .joinToString("\n") {
                "- [**${it.leagueId}**] ${it.leagueName}"
            }

        val messageToSend = """
            Leauges with ongoing tournaments
$ongoingTourneys
        """.trim()

        event.channel.sendMessage(messageToSend).complete()
    }

    override fun validate() {
        validateWordCount(1..1)
    }
}
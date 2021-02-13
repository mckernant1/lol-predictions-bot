package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.getTeamFromName
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RecordCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val words = getWordsFromMessage(event.message)
        val team1 = words[1].toUpperCase()
        val team2 = words.getOrNull(2)?.toUpperCase()
        val team = getTeamFromName(team1)
        val matches = getResults(team.homeLeagueCode, Int.MAX_VALUE)

        var matchesGroupedByTeam = matches
            .filter { it.team1.equals(team1, ignoreCase = true) || it.team2.equals(team1, ignoreCase = true) }
            .groupBy {
                if (it.team1.equals(team1, ignoreCase = true)) it.team2 else it.team1
            }

        if (team2 != null) {
            matchesGroupedByTeam = matchesGroupedByTeam.filter { (teamName, _) ->
                teamName.equals(team2, ignoreCase = true)
            }
        }

        val teamStrings = matchesGroupedByTeam
            .map { (teamName, matches) ->
                Record(teamName, matches.count { it.winner.equals(team1, ignoreCase = true) }, matches.size)
            }
            .sortedByDescending { it.getWinRatio() }
            .joinToString("\n") { "${it.team}: ${it.numWins}W, ${it.getLosses()}L - ${it.getWinRatio()}%" }
        val messageString = "Record for $team1 in ${team.homeLeagueCode}:\n$teamStrings"
        event.channel.sendMessage(messageString).complete()
    }

    data class Record(
        val team: String,
        val numWins: Int,
        val totalGames: Int
    ) {
        fun getWinRatio() = 100 * numWins / totalGames.toDouble()

        fun getLosses() = totalGames - numWins
    }

    override fun validate(): Boolean =
        validateWordCount(event, 2..3)
                && validateTeam(event, 1)
                && if (getWordsFromMessage(event.message).size == 3) validateTeam(event, 2) else true
}

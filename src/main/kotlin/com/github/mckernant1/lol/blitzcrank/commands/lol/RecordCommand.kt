package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.getTeamFromName
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.heimerdinger.schedule.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.ZonedDateTime

class RecordCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        val words = getWordsFromMessage(event.message)
        val team1Words = words[1].toUpperCase()
        val team2Words = words.getOrNull(2)?.toUpperCase()
        val team1 = getTeamFromName(team1Words)
        val matches = getResults(team1.homeLeagueCode, Int.MAX_VALUE)

        var matchesGroupedByTeam = matches
            .filter { it.team1.equals(team1.name, ignoreCase = true) || it.team2.equals(team1.name, ignoreCase = true) }
            .groupBy {
                if (it.team1.equals(team1.name, ignoreCase = true)) it.team2 else it.team1
            }

        if (team2Words != null) {
            val team2 = getTeamFromName(team2Words)
            matchesGroupedByTeam = matchesGroupedByTeam.filter { (teamName, _) ->
                teamName.equals(team2.name, ignoreCase = true)
            }
        }

        val records = matchesGroupedByTeam
            .map { (teamName, matches) ->
                Record(
                    teamName,
                    matches.first().date,
                    matches,
                    matches.count { it.winner.equals(team1.name, ignoreCase = true) },
                    matches.size
                )
            }
        val totalTeamWins = records.sumBy { it.numWins }
        val totalTeamLosses = records.sumBy { it.getLosses() }
        val teamStrings = records
            .sortedByDescending { it.mostRecentMatchDate }
            .joinToString("\n") { record ->
                "${record.team} - (${record.numWins}W - ${record.getLosses()}): \n" +
                        record.matches.joinToString("\n") { "\t${if (it.winner == record.team) "L" else "W"} - ${longDateFormat.format(it.date)}" } }
        val messageString = "Record for ${team1.name} in ${team1.homeLeagueCode} (${totalTeamWins}W - ${totalTeamLosses}L):\n$teamStrings"
        event.channel.sendMessage(messageString).complete()
    }

    data class Record(
        val team: String,
        val mostRecentMatchDate: ZonedDateTime,
        val matches: List<Match>,
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

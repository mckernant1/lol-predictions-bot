package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.github.mckernant1.lol.esports.api.Match
import com.github.mckernant1.math.round
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.Instant

class RecordCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    companion object {
        private const val LINE_SEPARATOR = "\n---------------------------------------------------\n"
    }

    override suspend fun execute() {
        // Todo: Need to update this to match the new api spec. We should specify a league here
        val region = words[1].uppercase()
        val team1Words = words[2].uppercase()
        val team2Words = words.getOrNull(3)?.uppercase()
        val team1 = apiClient.getTeamByCode(team1Words)
        val matches = getResults(region, Int.MAX_VALUE)

        var matchesGroupedByTeam = matches
            .filter { it.blueTeamId.equals(team1.teamId, ignoreCase = true) || it.redTeamId.equals(team1.teamId, ignoreCase = true) }
            .groupBy {
                if (it.blueTeamId.equals(team1.teamId, ignoreCase = true)) it.redTeamId else it.blueTeamId
            }

        if (team2Words != null) {
            val team2 = apiClient.getTeamByCode(team2Words)
            matchesGroupedByTeam = matchesGroupedByTeam.filter { (teamName, _) ->
                teamName.equals(team2.teamId, ignoreCase = true)
            }
        }

        val records = matchesGroupedByTeam
            .map { (teamName, matches) ->
                Record(
                    teamName,
                    matches.first().startTimeAsInstant(),
                    matches,
                    matches.count { it.winner.equals(team1.teamId, ignoreCase = true) },
                    matches.size
                )
            }
        val totalTeamWins: Int = records.sumOf { it.numWins }
        val totalTeamLosses: Int = records.sumOf { it.getLosses() }
        val teamStrings = records
            .sortedByDescending { it.mostRecentMatchDate }
            .joinToString(LINE_SEPARATOR) { record ->
                "${record.team} (${record.numWins}W - ${record.getLosses()}L): \n" +
                        record.matches.joinToString("\n") {
                            "\t${if (it.winner == record.team) "L" else "W"} - ${longDateFormat.format(it.startTimeAsInstant())}"
                        }
            }.ifEmpty { "There are no previous matches here :[" }
        val messageString =
            "Record for ${team1.name} ${if (team2Words != null) "vs ${apiClient.getTeamByCode(team2Words).name} " else ""}in ${region.uppercase()} (${totalTeamWins}W - ${totalTeamLosses}L):$LINE_SEPARATOR$teamStrings"
        messageString.chunked(2000).forEach {
            event.channel.sendMessage(it).complete()
        }
    }

    data class Record(
        val team: String,
        val mostRecentMatchDate: Instant,
        val matches: List<Match>,
        val numWins: Int,
        val totalGames: Int,
    ) {
        fun getWinRatio() = (100 * numWins / totalGames.toDouble()).round(1)

        fun getLosses() = totalGames - numWins
    }

    override fun validate() {
        validateWordCount(3..4)
        validateRegion(1)
        validateTeam(2)
        if (words.size == 4) validateTeam(2)
    }
}

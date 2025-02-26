package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.commons.extensions.math.DoubleAlgebra.round
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import java.time.Instant

class RecordCommand(event: CommandInfo) : DiscordCommand(event) {
    override fun execute() {
        val region = region.uppercase()
        val team1Words = event.options["team1"]!!.uppercase()
        val team2Words = event.options["team2"]?.uppercase()
        val team1 = apiClient.getTeamByCode(team1Words)
        val matches = getResults(region, Int.MAX_VALUE)

        var matchesGroupedByTeam = matches
            .filter {
                it.blueTeamId.equals(team1.teamId, ignoreCase = true) || it.redTeamId.equals(
                    team1.teamId,
                    ignoreCase = true
                )
            }
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
        val totalTeamLosses: Int = records.sumOf { it.numLosses }
        val teamStrings = records
            .sortedByDescending { it.mostRecentMatchDate }
            .joinToString(LINE_SEPARATOR) { record ->
                "${record.team} (${record.numWins}W - ${record.numLosses}L): \n" +
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
        val numLosses = totalGames - numWins
        val winRatio = (100 * numWins / totalGames.toDouble()).round(1)
    }

    override fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
        validateAndSetNumberPositive(options["number_of_matches"])
        validateTeam(options["team1"])
        if (options["team2"] != null) {
            validateTeam(options["team2"])
        }
    }

    companion object : CommandMetadata {
        private const val LINE_SEPARATOR = "\n---------------------------------------------------\n"
        override val commandString: String = "record"
        override val commandDescription: String = "Get the record of a team in a given tournament"
        override val commandData: CommandData = commandDataFromJson(
            """
                {
                  "name": "$commandString",
                  "type": 1,
                  "description": "$commandDescription",
                  "options": [
                    {
                      "name": "league_id",
                      "description": "The league to query",
                      "type": 3,
                      "required": true
                    },
                    {
                      "name": "team1",
                      "description": "The team who's record to get",
                      "type": 3,
                      "required": true
                    },
                    {
                      "name": "team2",
                      "description": "The team to compare against",
                      "type": 3,
                      "required": false
                    }
                  ]
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = RecordCommand(event)

    }
}

package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.commons.extensions.collections.SetTheory.cartesianProduct
import com.mckernant1.commons.extensions.math.DoubleAlgebra.round
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.Prediction
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.blitzcrank.utils.getSchedule
import com.mckernant1.lol.blitzcrank.utils.model.BotUser
import com.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class ReportCommand(
    event: CommandInfo,
    private var previous: Boolean = false
) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    constructor(event: MessageReceivedEvent, previous: Boolean) : this(event) {
        this.previous = previous
    }

    override fun execute() {
        val results = if (previous) getResults(region, numToGet) else getSchedule(region, numToGet)

        if (results.isEmpty()) {
            val message = "There are no matches to report"
            event.channel.sendMessage(message).complete()
            return
        }

        val users = getAllUsersForServer()

        val predictions = users.cartesianProduct(results)
            .mapNotNull { (user, result) ->
                val prediction = Prediction.getItem(user.getId(), result.matchId)
                    ?: return@mapNotNull null
                UserPrediction(user, prediction)
            }


        val predictionStrings = results.map { match ->
            val globalPredictions = Prediction.getAllPredictionsForMatch(match.matchId)
            "On ${mediumDateFormat.format(match.startTimeAsInstant())} **${match.blueTeamId}** vs **${match.redTeamId}**:\n" +
                    "${
                        if (match.winner == match.blueTeamId)
                            "\uD83D\uDC51 " else ""
                    }${match.blueTeamId}: ${
                        predictions.filter { it.prediction.matchId == match.matchId && it.prediction.prediction == match.blueTeamId }
                            .joinToString(" ") { it.botUser.getMentionable() }
                    }${
                        getGlobalPredictionRate(globalPredictions, match.blueTeamId)
                    }" +
                    "\n" +
                    "${
                        if (match.winner == match.redTeamId)
                            "\uD83D\uDC51 " else ""
                    }${match.redTeamId}: ${
                        predictions.filter { it.prediction.matchId == match.matchId && it.prediction.prediction == match.redTeamId }
                            .joinToString(" ") { it.botUser.getMentionable() }
                    }${
                        getGlobalPredictionRate(globalPredictions, match.redTeamId)
                    }"
        }
        val str = predictionStrings.joinToString("\n\n")
        when {
            predictionStrings.isEmpty() -> return
            str.length < 2000 -> event.channel.sendMessage(str).complete()
            else -> predictionStrings.forEach {
                event.channel.sendMessage(it).complete()
            }
        }
    }

    private data class UserPrediction(
        val botUser: BotUser,
        val prediction: Prediction
    )

    private fun getGlobalPredictionRate(predictions: List<Prediction>, teamName: String): String {
        return runCatching {
            if (predictions.isNotEmpty()) " ${
                (100 * predictions.count { it.prediction == teamName } / predictions.size.toDouble()).round(
                    1
                )
            }% of all users" else ""
        }.getOrElse { "" }
    }

    override fun validate() {
        validateWordCount(2..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    object Predictions : CommandMetadata {
        override val commandString: String = "predictions"
        override val commandDescription: String = "The predictions for the given league"
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
                  "name": "number_of_matches",
                  "description": "The number of matches to get",
                  "type": 3,
                  "required": false
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = ReportCommand(event, false)
    }

    object Report : CommandMetadata {
        override val commandString: String = "report"
        override val commandDescription: String = "Get a report on predictions for a given league"
        override val commandData: CommandData = commandDataFromJson("""
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
                  "name": "number_of_matches",
                  "description": "The number of matches to get",
                  "type": 3,
                  "required": false
                }
              ]
            }
        """.trimIndent())

        override fun create(event: CommandInfo): DiscordCommand = ReportCommand(event, true)
    }

}

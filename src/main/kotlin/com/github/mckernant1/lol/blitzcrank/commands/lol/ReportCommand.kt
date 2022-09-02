package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.extensions.collections.cartesianProduct
import com.github.mckernant1.extensions.math.round
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import com.github.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ReportCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    private var previous: Boolean = false

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

        val users = try {
            event.guild.members.map { it.id }
        } catch (e: IllegalStateException) {
            logger.info("IllegalStateException thrown ${e.message}")
            listOf(event.message.author.id)
        }.also { logger.info("User Ids in server: $it") }

        val predictions = users.cartesianProduct(results)
            .mapNotNull { (userId, result) ->
                Prediction.getItem(userId, result.matchId)
            }


        val predictionStrings = results.map { match ->
            val globalPredictions = Prediction.getAllPredictionsForMatch(match.matchId)
            "On ${mediumDateFormat.format(match.startTimeAsInstant())} **${match.blueTeamId}** vs **${match.redTeamId}**:\n" +
                    "${
                        if (match.winner == match.blueTeamId)
                            "\uD83D\uDC51 " else ""
                    }${match.blueTeamId}: ${
                        predictions.filter { it.matchId == match.matchId && it.prediction == match.blueTeamId }
                            .joinToString(" ") { "<@${it.userId}>" }
                    }${
                        getGlobalPredictionRate(globalPredictions, match.blueTeamId)
                    }" +
                    "\n" +
                    "${
                        if (match.winner == match.redTeamId)
                            "\uD83D\uDC51 " else ""
                    }${match.redTeamId}: ${
                        predictions.filter { it.matchId == match.matchId && it.prediction == match.redTeamId }
                            .joinToString(" ") { "<@${it.userId}>" }
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

}

package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.collections.cartesianProduct
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import com.github.mckernant1.lol.blitzcrank.utils.predictionsTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ReportCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    private var previous: Boolean = false

    constructor(event: MessageReceivedEvent, previous: Boolean) : this(event) {
        this.previous = previous
    }

    override suspend fun execute() {
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
                predictionsTable.getItem(userId, result.id)
            }



        val predictionString = results.joinToString("\n\n") { match ->
            val globalPredictions = predictionsTable.getAllPredictionsForMatch(match.id)
            "On ${shortDateFormat.format(match.date)} **${match.team1}** vs **${match.team2}**:\n" +
                    "${
                        if (match.winner == match.team1)
                            "\uD83D\uDC51 " else ""
                    }${match.team1}: ${
                        predictions.filter { it.matchId == match.id && it.prediction == match.team1 }
                            .joinToString(" ") { "<@${it.userId}>" }
                    }${
                        getGlobalPredictionRate(globalPredictions, match.team1)
                    }" +
                    "\n" +
                    "${
                        if (match.winner == match.team2)
                            "\uD83D\uDC51 " else ""
                    }${match.team2}: ${
                        predictions.filter { it.matchId == match.id && it.prediction == match.team2 }
                            .joinToString(" ") { "<@${it.userId}>" }
                    }${
                        getGlobalPredictionRate(globalPredictions, match.team2)
                    }"
        }

        if (predictionString.isNotEmpty()) {
            event.channel.sendMessage(predictionString).complete()
        }
    }

    private fun getGlobalPredictionRate(predictions: List<Prediction>, teamName: String): String {
        return runCatching {
            if (predictions.isNotEmpty()) " ${100 * predictions.count { it.prediction == teamName } / predictions.size.toDouble()}% of all users" else ""
        }.getOrElse { "" }
    }

    override fun validate(): Boolean {
        return validateWordCount(2..3) && validateRegion(1) && validateNumberOfMatches(2)
    }

}

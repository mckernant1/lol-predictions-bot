package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
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

        val predictions = users
            .flatMap { predictionsTable.getUsersPredictions(it) }
            .filter { userPrediction -> results.map { it.id }.contains(userPrediction.matchId) }

        val predictionString = results.joinToString("\n\n") { match ->
            "Match **${match.team1}** vs **${match.team2}**:\n" +
                    "${
                        if (match.winner == match.team1)
                            "\uD83D\uDC51 " else ""
                    }${match.team1}: ${
                        predictions.filter { it.matchId == match.id && it.prediction == match.team1 }
                            .joinToString(", ") { "<@${it.userId}>" }
                    }\n" +
                    "${
                        if (match.winner == match.team2)
                            "\uD83D\uDC51 " else ""
                    }${match.team2}: ${
                        predictions.filter { it.matchId == match.id && it.prediction == match.team2 }
                            .joinToString(", ") { "<@${it.userId}>" }
                    }"
        }

        if (predictionString.isNotEmpty()) {
            event.channel.sendMessage(predictionString).complete()
        }
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)
    }

}

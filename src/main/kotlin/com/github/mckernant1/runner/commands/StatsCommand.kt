package com.github.mckernant1.runner.commands

import com.github.mckernant1.runner.utils.getMatchesWithThreads
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.litote.kmongo.`in`

class StatsCommand(event: MessageReceivedEvent) : MongoCommand(event) {
    override suspend fun execute() {
        val results = getMatchesWithThreads(region).matches
        val users = event.guild.members.map { it.id }
        val serverMatches = collection.find(
            Prediction::userId `in` users
        )

        val resultString = users.map { userId ->
            val numberPredicted = serverMatches.count { prediction ->
                prediction.userId == userId && results.any { it.id == prediction.matchId }
            }
            val numberCorrect = serverMatches.count { prediction ->
                val relevantResult = results.find { prediction.matchId == it.id }
                    ?: return@count false
                return@count prediction.userId == userId && relevantResult.winner == prediction.prediction
            }
            PredictionRatio(userId, numberPredicted, numberCorrect)
        }.filter { it.numberPredicted != 0 }
            .sortedByDescending { it.numberCorrect / it.numberPredicted.toDouble() }
            .joinToString("\n") {
                "<@${it.userId}> predicted ${it.numberCorrect} out of ${it.numberPredicted} for a correct prediction rate of ${100 * it.numberCorrect / it.numberPredicted.toDouble()}%"
            }
        event.channel.sendMessage("Prediction results are:\n$resultString").complete()
    }

    private data class PredictionRatio(
        val userId: String,
        val numberPredicted: Int,
        val numberCorrect: Int
    )

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2) && validateRegion(event, 1)
    }
}
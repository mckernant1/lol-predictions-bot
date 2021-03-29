package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.collections.cartesianProduct
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getMatchesWithThreads
import com.github.mckernant1.lol.blitzcrank.utils.predictionsTable
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.time.ZoneId
import java.time.ZonedDateTime

class StatsCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        var results = getMatchesWithThreads(region).matches
            .sortedByDescending { it.date }.dropWhile {
                it.date > ZonedDateTime.now(ZoneId.of("UTC"))
            }
        results = if (numToGet != null) {
            results.take(numToGet!!)
        } else {
            results
        }
        val users = try {
            event.guild.members.map { it.id }
        } catch (e: IllegalStateException) {
            listOf(event.message.author.id)
        }


        val serverMatches = users.cartesianProduct(results)
            .mapNotNull { (userId, match) ->
                predictionsTable.getItem(userId, match.id)
            }

        val resultString = users.map { userId ->
            val numberPredicted = serverMatches.count { prediction ->
                prediction.userId == userId && results.any { it.id == prediction.matchId }
            }
            val numberCorrect = serverMatches.count { prediction ->
                val relevantResult = results.find { prediction.matchId == it.id }
                    ?: return@count false
                return@count prediction.userId == userId && relevantResult.winner == prediction.prediction
            }
            return@map PredictionRatio(userId, numberPredicted, numberCorrect)
        }.filter { it.numberPredicted != 0 }
            .sortedByDescending { it.getPredictionPercentage() }
            .joinToString("\n") {
                "<@${it.userId}> predicted ${it.numberCorrect} out of ${it.numberPredicted} for a correct prediction rate of **${it.getPredictionPercentage()}%**"
            }
        if (resultString.isBlank()) {
            event.channel.sendMessage("There are no past matches for this tournament").complete()
        } else {
            event.channel.sendMessage("Prediction results for $region are:\n$resultString").complete()
        }
    }

    private data class PredictionRatio(
        val userId: String,
        val numberPredicted: Int,
        val numberCorrect: Int
    ) {
        fun getPredictionPercentage() = 100 * numberCorrect / numberPredicted.toDouble()
    }

    override fun validate(): Boolean {
        return validateWordCount(2..3) && validateRegion(1) && validateNumberOfMatches(2)
    }
}

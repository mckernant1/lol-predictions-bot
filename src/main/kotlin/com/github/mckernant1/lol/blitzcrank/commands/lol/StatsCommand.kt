package com.github.mckernant1.lol.blitzcrank.commands.lol

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


        val serverMatches = users.flatMap {
            predictionsTable.getUsersPredictions(it)
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
            .sortedByDescending { it.numberCorrect / it.numberPredicted.toDouble() }
            .joinToString("\n") {
                "<@${it.userId}> predicted ${it.numberCorrect} out of ${it.numberPredicted} for a correct prediction rate of **${100 * it.numberCorrect / it.numberPredicted.toDouble()}%**"
            }
        event.channel.sendMessage("Prediction results are:\n$resultString").complete()
    }

    private data class PredictionRatio(
        val userId: String,
        val numberPredicted: Int,
        val numberCorrect: Int
    )

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)
    }
}

package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.extensions.collections.cartesianProduct
import com.github.mckernant1.extensions.math.round
import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.model.BotUser
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class StatsCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        var results = getResults(region, 100)

        results = if (numToGet != null) {
            results.take(numToGet!!)
        } else {
            results
        }
        val users = getAllUsersForServer()

        val serverMatches = users.cartesianProduct(results)
            .mapNotNull { (user, match) ->
                Prediction.getItem(user.getId(), match.matchId)
            }

        val resultString = users.map { user ->
            val numberPredicted = serverMatches.count { prediction ->
                prediction.userId == user.getId() && results.any { it.matchId == prediction.matchId }
            }
            val numberCorrect = serverMatches.count { prediction ->
                val relevantResult = results.find { prediction.matchId == it.matchId }
                    ?: return@count false
                return@count prediction.userId == user.getId() && relevantResult.winner == prediction.prediction
            }
            return@map PredictionRatio(user, numberPredicted, numberCorrect)
        }.filter { it.numberPredicted != 0 }
            .sortedByDescending { it.numberCorrect }
            .map {
                "${it.user.getMentionable()} predicted ${it.numberCorrect} out of ${it.numberPredicted} for a correct prediction rate of **${it.getPredictionPercentage()}%**"
            }
        val str = resultString.joinToString("\n") { it }
        if (str.isBlank()) {
            event.channel.sendMessage("There are no past matches for this tournament").complete()
        } else if (str.length < 2000) {
            event.channel.sendMessage("Prediction results for $region are:\n$str").complete()
        } else {
            event.channel.sendMessage("Prediction results for $region are:").complete()
            resultString.forEach {
                event.channel.sendMessage(it).complete()
            }
        }
    }

    private data class PredictionRatio(
        val user: BotUser,
        val numberPredicted: Int,
        val numberCorrect: Int,
    ) {
        fun getPredictionPercentage() = (100 * numberCorrect / numberPredicted.toDouble()).round(1)
    }

    override fun validate() {
        validateWordCount(2..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    companion object : CommandMetadata {
        override val commandString: String = "stats"
        override val commandDescription: String = "The stats for a given league"
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
                    }
                  ]
                }
            """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = StatsCommand(event)
    }
}

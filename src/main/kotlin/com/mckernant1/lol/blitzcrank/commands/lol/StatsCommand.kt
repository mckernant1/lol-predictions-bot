package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.commons.extensions.collections.SetTheory.cartesianProduct
import com.mckernant1.commons.extensions.math.DoubleAlgebra.round
import com.mckernant1.commons.standalone.MeasureSuspend.measureOperation
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.Prediction
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.coroutineScope
import com.mckernant1.lol.blitzcrank.utils.endDateAsDate
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.blitzcrank.utils.model.BotUser
import com.mckernant1.lol.esports.api.models.Match
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.internal.interactions.CommandDataImpl
import java.time.ZoneId
import java.time.ZonedDateTime

class StatsCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {

    override suspend fun execute(): Unit {
        val results: List<Match> = when (Timeframe.valueOf(event.options["timeframe"]!!)) {
            Timeframe.Tournament -> getResults(region, 100)
            Timeframe.Year -> apiClient.getTournamentsForLeague(region.uppercase()).filter {
                it.endDateAsDate()?.atZone(ZoneId.of("UTC"))?.year == ZonedDateTime.now().year
            }.flatMap {
                apiClient.getMatchesForTournament(it.tournamentId)
            }
            Timeframe.Split -> {
                val splitIdentifier: String = apiClient.getMostRecentTournament(region.uppercase()).let {
                    val groups = tournamentIdRegex.matchEntire(it.tournamentId)?.groups
                        ?: throw InvalidCommandException("Tournament name ${it.tournamentId} could not be parsed")
                    val region = groups["region"]?.value!!
                    val year = groups["year"]?.value!!
                    val split = groups["split"]?.value!!
                    return@let "${region}_${year}_${split}"
                }

                apiClient.getTournamentsForLeague(region.uppercase())
                    .filter { it.tournamentId.contains(splitIdentifier) }
                    .flatMap {
                        apiClient.getMatchesForTournament(it.tournamentId)
                    }
            }

        }


        val users = getAllUsersForServer()

        val (duration, serverMatches) = measureOperation {
            users
                .cartesianProduct(results)
                .map { (user, match) ->
                    coroutineScope.async { Prediction.getItem(user.getId(), match.matchId) }
                }
                .awaitAll()
                .filterNotNull()
        }

        logger.info("Getting ${serverMatches.size} matches took ${duration.toMillis()}ms")

        val resultString = users.asSequence()
            .map { user ->
                val numberPredicted = serverMatches.count { prediction ->
                    prediction.userId == user.getId() && results.any { it.matchId == prediction.matchId }
                }
                val numberCorrect = serverMatches.count { prediction ->
                    val relevantResult = results.find { prediction.matchId == it.matchId }
                        ?: return@count false
                    return@count prediction.userId == user.getId() && relevantResult.winner == prediction.prediction
                }
                return@map PredictionRatio(user, numberPredicted, numberCorrect)
            }
            .filter { it.numberPredicted != 0 }
            .sortedByDescending { it.numberCorrect }
            .map {
                "${it.user.getMentionable()} predicted ${it.numberCorrect} out of ${it.numberPredicted} for a correct prediction rate of **${it.getPredictionPercentage()}%**"
            }
            .toList()
        val str = resultString.joinToString("\n") { it }
        if (str.isBlank()) {
            event.channel.sendMessage("There are no completed predictions for $region to display").submit().await()
        } else if (str.length < 2000) {
            event.channel.sendMessage("Prediction results for $region are:\n$str").submit().await()
        } else {
            event.channel.sendMessage("Prediction results for $region are:").submit().await()
            resultString.forEach {
                event.channel.sendMessage(it).submit().await()
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

    override suspend fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
    }

    private enum class Timeframe {
        Tournament,
        Year,
        Split
    }

    companion object : CommandMetadata {
        // Should look like one of these
        // LEC_2022_Summer
        // LEC_2022_Summer_Playoffs
        private val tournamentIdRegex =
            Regex("(?<region>\\w+)_(?<year>\\d+)_(?<split>[a-zA-Z]+)(_(?<tournament>[a-zA-Z\\d]+))?")
        override val commandString: String = "stats"
        override val commandDescription: String = "The stats for a given league"
        override val commandData: CommandData = CommandDataImpl(commandString, commandDescription)
            .addOption(OptionType.STRING, "league_id", "The league to query", true)
            .addOptions(
                OptionData(OptionType.STRING, "timeframe", "What timeframe for stats", true)
                    .addChoice("Tournament", "Tournament")
                    .addChoice("Split", "Split")
                    .addChoice("Year", "Year")
            )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand = StatsCommand(event, userSettings)
    }
}

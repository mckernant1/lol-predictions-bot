package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.extensions.collections.cartesianProduct
import com.github.mckernant1.extensions.math.round
import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.exceptions.InvalidCommandException
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import com.github.mckernant1.lol.blitzcrank.utils.endDateAsDate
import com.github.mckernant1.lol.blitzcrank.utils.getResults
import com.github.mckernant1.lol.blitzcrank.utils.model.BotUser
import com.github.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import java.time.ZoneId
import java.time.ZonedDateTime

class StatsCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        val results: List<Match> = when (Timeframe.valueOf(words[2])) {
            Timeframe.Tournament -> getResults(region, 100)
            Timeframe.Year -> apiClient.getTournamentsForLeague(region).filter {
                it.endDateAsDate()?.atZone(ZoneId.of("UTC"))?.year == ZonedDateTime.now().year
            }.flatMap {
                apiClient.getMatchesForTournament(it.tournamentId)
            }
            Timeframe.Split -> {
                val splitIdentifier = apiClient.getMostRecentTournament(region).let {
                    val groups = tournamentIdRegex.matchEntire(it.tournamentId)?.groups
                        ?: throw InvalidCommandException("Tournament name ${it.tournamentId} could not be parsed")
                    val region = groups["region"]?.value!!
                    val year = groups["year"]?.value!!
                    val split = groups["split"]?.value!!
                    return@let "${region}_${year}_${split}"
                }

                apiClient.getTournamentsForLeague(region)
                    .filter { it.tournamentId.contains(splitIdentifier) }
                    .flatMap {
                        apiClient.getMatchesForTournament(it.tournamentId)
                    }
            }

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
            Regex("(?<region>[a-zA-Z]+)_(?<year>\\d+)_(?<split>[a-zA-Z]+)(_(?<tournament>[a-zA-Z]+))?")
        override val commandString: String = "stats"
        override val commandDescription: String = "The stats for a given league"
        override val commandData: CommandData = CommandData(commandString, commandDescription)
            .addOption(OptionType.STRING, "league_id", "The league to query", true)
            .addOptions(
                OptionData(OptionType.STRING, "timeframe", "What timeframe for stats", true)
                    .addChoice("Tournament", "Tournament")
                    .addChoice("Split", "Split")
                    .addChoice("Year", "Year")
            )

        override fun create(event: CommandInfo): DiscordCommand = StatsCommand(event)
    }
}

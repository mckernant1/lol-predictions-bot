package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.mckernant1.lol.esports.api.models.Match
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class ResultsCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {

    override suspend fun execute() {
        val matches = getResults(region, numToGet)
        if (matches.isEmpty()) {
            val message = "There are no results"
            event.channel.sendMessage(message).submit().await()
            return
        }
        val replyString =
            formatResultsReply(matches, region)
        event.channel.sendMessage(replyString)
            .setSuppressEmbeds(true)
            .submit().await()
    }

    override suspend fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
        validateAndSetNumberPositive(options["number_of_matches"])
    }

    private fun formatResultsReply(
        matches: List<Match>,
        region: String,
    ): String {
        val sb = StringBuilder()
        sb.appendLine("The last ${matches.size} matches in ${region.uppercase()} were: ")
        matches.forEach {
            sb.append(mediumDateFormat.format(it.startTimeAsInstant()))
            sb.append(" ")

            if (it.winner == it.blueTeamId) {
                sb.append("||$CROWN|| ")
            } else {
                sb.append("||$THUMBS_DOWN|| ")
            }

            sb.append("**${it.blueTeamId}** vs **${it.redTeamId}**")

            if (it.winner == it.redTeamId) {
                sb.append(" ||$CROWN||")
            } else {
                sb.append(" ||$THUMBS_DOWN||")
            }

            if (!it.vod.isNullOrBlank()) {
                sb.append(" ([Vod](${it.vod}))")
            }

            if (!it.highlight.isNullOrBlank()) {
                sb.append(" ([Highlight](${it.highlight}))")
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    companion object : CommandMetadata {
        private const val CROWN = "\uD83D\uDC51"
        private const val THUMBS_DOWN = "\uD83D\uDC4E"
        override val commandString: String = "results"
        override val commandDescription: String = "The results for the given league"
        override val commandData: CommandData = CommandDataImpl(commandString, commandDescription)
            .addOption(OptionType.STRING, "league_id", "The league to query", true)
            .addOption(OptionType.INTEGER, "number_of_matches", "The number of matches to get")

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand = ResultsCommand(event, userSettings)
    }
}



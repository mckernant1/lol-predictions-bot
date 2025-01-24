package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.commons.extensions.boolean.falseIfNull
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.getResults
import com.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class ResultsCommand(event: CommandInfo) : DiscordCommand(event) {

    override fun execute() {
        val matches = getResults(region, numToGet)
        val showSpoilers = event.options["spoilers"]?.toBoolean().falseIfNull()
        if (matches.isEmpty()) {
            val message = "There are no results"
            event.channel.sendMessage(message).complete()
            return
        }
        val replyString =
            formatResultsReply(matches, region, showSpoilers)
        event.channel.sendMessage(replyString)
            .setSuppressEmbeds(true)
            .complete()
    }

    override fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
        validateAndSetNumberPositive(options["number_of_matches"])
    }

    private fun formatResultsReply(
        matches: List<Match>,
        region: String,
        showSpoilers: Boolean,
    ): String {
        val sb = StringBuilder()
        sb.appendLine("The last ${matches.size} matches in ${region.uppercase()} were: ")
        matches.forEach {
            sb.append(mediumDateFormat.format(it.startTimeAsInstant()))
            sb.append(" ")

            if (showSpoilers && it.winner == it.blueTeamId) {
                sb.append("$CROWN ")
            }

            sb.append("**${it.blueTeamId}** vs **${it.redTeamId}**")

            if (showSpoilers && it.winner == it.redTeamId) {
                sb.append(" $CROWN")
            }

            if (it.vod != null) {
                sb.append(" ([Vod](${it.vod}))")
            }

            if (it.highlight != null) {
                sb.append(" ([Highlight](${it.highlight}))")
            }
            sb.appendLine()
        }
        return sb.toString()
    }

    companion object : CommandMetadata {
        private const val CROWN = "\uD83D\uDC51"
        override val commandString: String = "results"
        override val commandDescription: String = "The results for the given league"
        override val commandData: CommandData = CommandDataImpl(commandString, commandDescription)
            .addOption(OptionType.STRING, "league_id", "The league to query", true)
            .addOption(OptionType.INTEGER, "number_of_matches", "The number of matches to get")
            .addOption(OptionType.BOOLEAN, "spoilers", "Show the results of the matches", false)

        override fun create(event: CommandInfo): DiscordCommand = ResultsCommand(event)
    }
}



package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.utils.getSchedule
import com.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.internal.interactions.CommandDataImpl

class ScheduleCommand(event: CommandInfo) : DiscordCommand(event) {

    override fun execute() {
        val matches = if ("team_id" in event.options) {
            val teamId = event.options["team_id"]?.uppercase()
            getSchedule(region, numToGet ?: 5) {
                it.blueTeamId == teamId || it.redTeamId == teamId
            }
        } else {
            getSchedule(region, numToGet)
        }
        if (matches.isEmpty()) {
            val message = "There are no matches listed"
            event.channel.sendMessage(message).complete()
            return
        }
        val replyString = formatScheduleReply(matches, region, event.options["team_id"])
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate(options: Map<String, String>) {
        validateAndSetRegion(options["league_id"])
        validateAndSetNumberPositive(options["number_of_matches"])
        if (options.containsKey("team_id")) {
            validateTeam(options["team_id"])
        }
    }

    private fun formatScheduleReply(
        matches: List<Match>,
        region: String,
        teamId: String? = null
    ): String {
        val sb = StringBuilder()
        sb.append("The next ${matches.size} matches in **${region.uppercase()}**")
        if (teamId != null) {
            sb.append(" for **${teamId.uppercase()}**")
        }
        sb.appendLine(" are: ")
        matches.forEach {
            sb.appendLine("${longDateFormat.format(it.startTimeAsInstant())}: **${it.blueTeamId}** vs **${it.redTeamId}**")
        }
        return sb.toString()
    }

    companion object : CommandMetadata {
        override val commandString: String = "schedule"
        override val commandDescription: String = "Get Schedules for the given league"
        override val commandData: CommandData = CommandDataImpl(commandString, commandDescription)
            .addOption(OptionType.STRING, "league_id", "The league to query", true)
            .addOption(OptionType.INTEGER, "number_of_matches", "The number of matches", false)
            .addOption(OptionType.STRING, "team_id", "Get the schedule of a particular team", false)

        override fun create(event: CommandInfo): DiscordCommand = ScheduleCommand(event)
    }
}





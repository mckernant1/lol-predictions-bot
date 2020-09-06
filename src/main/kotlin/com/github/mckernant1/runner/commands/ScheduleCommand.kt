package com.github.mckernant1.runner.commands

import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.runner.utils.getSchedule
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ScheduleCommand(event: MessageReceivedEvent) : DiscordCommand(event) {


    override suspend fun execute() {
        val matches = getSchedule(region, numToGet)
        val replyString = formatScheduleReply(matches, numToGet, region)
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)

    }

    private fun formatScheduleReply(matches: List<Match>, numToGet: Int, region: String): String {
        val sb = StringBuilder()
        sb.appendln("The next $numToGet matches in $region are: ")
        matches.forEach {
            sb.appendln("${dateFormat.format(it.date)}: ${it.team1} vs ${it.team2}")
        }
        return sb.toString()
    }
}





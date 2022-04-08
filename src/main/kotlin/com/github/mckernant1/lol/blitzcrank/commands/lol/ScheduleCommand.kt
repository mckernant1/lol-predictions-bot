package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import com.github.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import com.github.mckernant1.lol.esports.api.models.Match
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ScheduleCommand(event: MessageReceivedEvent) : DiscordCommand(event) {


    override suspend fun execute() {
        val matches = getSchedule(region, numToGet)
        if (matches.isEmpty()) {
            val message = "There are no matches listed"
            event.channel.sendMessage(message).complete()
            return
        }
        val replyString = formatScheduleReply(matches, region)
        event.channel.sendMessage(replyString).queue()
    }

    override fun validate() {
        validateWordCount(2..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    private fun formatScheduleReply(matches: List<Match>, region: String): String {
        val sb = StringBuilder()
        sb.appendLine("The next ${matches.size} matches in $region are: ")
        matches.forEach {
            sb.appendLine("${longDateFormat.format(it.startTimeAsInstant())}: **${it.blueTeamId}** vs **${it.redTeamId}**")
        }
        return sb.toString()
    }
}





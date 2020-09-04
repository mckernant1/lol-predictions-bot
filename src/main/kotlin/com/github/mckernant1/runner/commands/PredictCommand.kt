package com.github.mckernant1.runner.commands

import com.github.mckernant1.lolapi.schedule.Match
import com.github.mckernant1.runner.utils.getScheduleForNextDay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*
import kotlin.concurrent.schedule

class PredictCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override suspend fun execute() {
        val matches = getScheduleForNextDay(region)
        val replies = formatReplies(matches)
        replies.map { (msg, date) ->
            event.channel.sendMessage(msg).queue {
                it.addReaction("\uD83D\uDD35").queue()
                it.addReaction("\uD83D\uDD34").queue()
                Timer().schedule(500, 60) {
                    if (Date() > date) {
                        cancel()
                    }
                }
            }
        }

    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2) && validateRegion(event, 1)
    }

    private fun formatReplies(matches: List<Match>): List<Pair<String, Date>> {
        return matches.map {
            "${it.date}: \uD83D\uDD35 ${it.team1} vs ${it.team2} \uD83D\uDD34" to it.date
        }
    }
}





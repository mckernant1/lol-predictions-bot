package com.github.mckernant1.runner.commands

import com.github.mckernant1.runner.utils.getScheduleForNextDay
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.text.DateFormat
import java.time.Duration
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

val sharedStatePredictionsMap = ConcurrentHashMap<String, ConcurrentHashMap<String, List<String>>>()
const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
const val RED_TEAM_EMOJI = "\uD83D\uDD34"

class PredictCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    private val timer = Timer()

    override suspend fun execute() {
        val matches = getScheduleForNextDay(region, numToGet)
        matches.map { match ->
            val dateFormat = DateFormat.getDateTimeInstance(
                DateFormat.FULL, DateFormat.LONG
            )
            dateFormat.timeZone = TimeZone.getTimeZone(ZoneId.of("America/Los_Angeles"))
            val msg = "${dateFormat.format(match.date)}: \uD83D\uDD35 ${match.team1} vs ${match.team2} \uD83D\uDD34"
            val date = match.date
            event.channel.sendMessage(msg).queue { message ->
                message.addReaction(BLUE_TEAM_EMOJI).queue()
                message.addReaction(RED_TEAM_EMOJI).queue()
                timer.schedule(500, Duration.ofMinutes(1).toMillis()) {
                    println("Checking for reaction updates")
                    if (Date() >= date) {
                        val matchMap = sharedStatePredictionsMap[match.id] ?: ConcurrentHashMap()
                        val messageStr = matchMap.map { (team, users) ->
                            "$team was picked by $users"
                        }.reduce { acc, s -> "$acc\n$s" }
                        event.channel.sendMessage(messageStr)
                        cancel()
                    }
                    message.retrieveReactionUsers(BLUE_TEAM_EMOJI).queue { users ->
                        val usernames = users.map { it.name }
                        println("${match.team1} was picked by $usernames")
                        sharedStatePredictionsMap.getOrPut(match.id) { ConcurrentHashMap() }[match.team1] = usernames
                    }
                    message.retrieveReactionUsers(RED_TEAM_EMOJI).queue { users ->
                        val usernames = users.map { it.name }
                        println("${match.team2} was picked by $usernames")
                        sharedStatePredictionsMap.getOrPut(match.id) { ConcurrentHashMap() }[match.team2] = usernames
                    }
                    println("Resulting $sharedStatePredictionsMap")
                }
            }
        }

    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2, 1)
    }
}


package com.github.mckernant1.runner.commands

import com.github.mckernant1.runner.utils.getScheduleForNextDay
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.text.DateFormat
import java.time.Duration
import java.time.ZoneId
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.schedule

val sharedStatePredictionsMap = ConcurrentHashMap<String, ConcurrentHashMap<String, List<String>>>()

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
                timer.schedule(5000, Duration.ofMinutes(1).toMillis()) {
                    message.retrieveReactionUsers(BLUE_TEAM_EMOJI).complete().also { users ->
                        updateState(users, match.team1, match.id)
                    }
                    message.retrieveReactionUsers(RED_TEAM_EMOJI).complete().also { users ->
                        updateState(users, match.team2, match.id)
                    }
                    logger.info("Shared State map $sharedStatePredictionsMap")
                    if (Date() >= date) {
                        logger.info("Match ${match.team1} vs ${match.team2} is starting")
                        val matchMap = sharedStatePredictionsMap[match.id] ?: ConcurrentHashMap()
                        val messageStr = matchMap.map { (team, users) ->
                            "$team was picked by ${users.joinToString(separator = ", ") { "<@$it>" }}"
                        }.fold("") { acc, s -> "$acc\n$s" }.also { logger.info(it) }
                        if (messageStr.isNotBlank()) {
                            event.channel.sendMessage("Match ${match.team1} vs ${match.team2} is starting: $messageStr").complete()
                        }
                        cancel()
                    }
                }
            }
        }

    }


    private fun updateState(users: List<User>, team: String, matchId: String) {
        val usernames = users.filter { !it.isBot }.map { it.id }
        logger.info("$team was picked by $usernames")
        sharedStatePredictionsMap.getOrPut(matchId) { ConcurrentHashMap() }[team] = usernames
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2, 1)
    }

    companion object {
        const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
        const val RED_TEAM_EMOJI = "\uD83D\uDD34"
    }
}


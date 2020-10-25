package com.github.mckernant1.runner.commands

import com.github.mckernant1.runner.utils.Prediction
import com.github.mckernant1.runner.utils.collection
import com.github.mckernant1.runner.utils.getSchedule
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.time.Duration
import java.util.Timer
import kotlin.concurrent.schedule


class PredictCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override suspend fun execute() {
        val matches = getSchedule(region, numToGet)
        if (matches.isEmpty()) {
            val message = "There are no matches to predict"
            event.channel.sendMessage(message).complete()
            return
        }

        matches.forEach { match ->
            val date = match.date
            val msg = "${dateFormat.format(date)}: \uD83D\uDD35 **${match.team1}** vs **${match.team2}** \uD83D\uDD34\n" +
                    "(This message will be deleted in 5 mins)"
            val message = event.channel.sendMessage(msg).complete()
            message.addReaction(BLUE_TEAM_EMOJI).complete()
            message.addReaction(RED_TEAM_EMOJI).complete()
            timer.schedule(Duration.ofMinutes(5).toMillis()) {
                val blueTeamUsers =
                    message.retrieveReactionUsers(BLUE_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val redTeamUsers =
                    message.retrieveReactionUsers(RED_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val predictions =
                    mapOf(match.team1 to blueTeamUsers, match.team2 to redTeamUsers).map { (team, users) ->
                        users.map { Prediction(match.id, it, team) }
                    }.flatten().also { logger.info(it.toString()) }
                predictions.forEach {
                    collection.deleteMany(
                        and(
                            Prediction::userId eq it.userId,
                            Prediction::matchId eq it.matchId
                        )
                    )
                    collection.insertOne(it)
                }
                message.delete().complete()
            }
        }
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)
    }

    companion object {
        private val timer = Timer()
        const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
        const val RED_TEAM_EMOJI = "\uD83D\uDD34"
    }
}


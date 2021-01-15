package com.github.mckernant1.lol.blitzcrank.commands

import com.github.mckernant1.lol.blitzcrank.utils.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.collection
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.litote.kmongo.and
import org.litote.kmongo.eq
import java.time.Duration


class PredictCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override suspend fun execute() = coroutineScope {
        val matches = getSchedule(region, numToGet)
        if (matches.isEmpty()) {
            val message = "There are no matches to predict"
            event.channel.sendMessage(message).complete()
            return@coroutineScope
        }

        matches.map { match ->
            val date = match.date
            val msg =
                "${dateFormat.format(date)}: \uD83D\uDD35 **${match.team1}** vs **${match.team2}** \uD83D\uDD34\n(This message will be deleted in 5 mins)"
            val message = event.channel.sendMessage(msg).complete()
            message.addReaction(BLUE_TEAM_EMOJI).complete()
            message.addReaction(RED_TEAM_EMOJI).complete()

            launch {
                logger.info("Starting Thread to wait for 5 mins then check the result")
                delay(Duration.ofMinutes(5).toMillis())
                logger.info("Done sleeping")
                val blueTeamUsers =
                    message.retrieveReactionUsers(BLUE_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val redTeamUsers =
                    message.retrieveReactionUsers(RED_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val predictions =
                    mapOf(match.team1 to blueTeamUsers, match.team2 to redTeamUsers).map { (team, users) ->
                        users.map { Prediction(match.id, it, team) }
                    }.flatten().also { logger.info("Saving prediction: $it") }
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
        }.forEach { it.join() }
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..3) && validateRegion(event, 1) && validateNumberOfMatches(event, 2)
    }

    companion object {
        const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
        const val RED_TEAM_EMOJI = "\uD83D\uDD34"
    }
}


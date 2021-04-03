package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import com.github.mckernant1.lol.blitzcrank.utils.predictionsTable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
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
                "${longDateFormat.format(date)}: \uD83D\uDD35 **${match.team1}** vs **${match.team2}** \uD83D\uDD34\n(This message will delete itself in 5 mins. DO NOT delete yourself)"
            val message = event.channel.sendMessage(msg).complete()
            message.addReaction(BLUE_TEAM_EMOJI).complete()
            message.addReaction(RED_TEAM_EMOJI).complete()

            return@map launch {
                logger.info("Starting Thread to wait for 5 mins then check the result")
//                delay(Duration.ofSeconds(30).toMillis()) // For Testing
                delay(Duration.ofMinutes(5).toMillis())

                logger.info("Done sleeping")
                val blueTeamUsers =
                    message.retrieveReactionUsers(BLUE_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val redTeamUsers =
                    message.retrieveReactionUsers(RED_TEAM_EMOJI).complete().filter { !it.isBot }.map { it.id }
                val predictions = mapOf(match.team1 to blueTeamUsers, match.team2 to redTeamUsers)
                    .map { (team, users) ->
                        users.map { Prediction(matchId = match.id, userId = it, prediction = team) }
                    }.flatten().also { logger.info("Saving prediction: $it") }
                predictions.forEach { predictionsTable.putItem(it) }
                message.delete().complete()
            }
        }.forEach { it.join() }
        event.channel.sendMessage(
            "Predictions have been recorded for ${matches.size} ${if (matches.size == 1) "match" else "matches"} in the $region").complete()
    }

    override fun validate(): Boolean {
        return validateWordCount(2..3) && validateRegion(1) && validateNumberPositive(2)
    }

    companion object {
        const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
        const val RED_TEAM_EMOJI = "\uD83D\uDD34"
    }
}


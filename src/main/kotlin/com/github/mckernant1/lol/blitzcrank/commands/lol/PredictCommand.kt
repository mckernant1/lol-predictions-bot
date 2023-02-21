package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import com.github.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.github.mckernant1.lol.blitzcrank.utils.getSchedule
import com.github.mckernant1.lol.blitzcrank.utils.startTimeAsInstant
import java.time.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class PredictCommand(event: CommandInfo) : DiscordCommand(event) {
    constructor(event: SlashCommandEvent) : this(CommandInfo(event))
    constructor(event: MessageReceivedEvent) : this(CommandInfo(event))

    override fun execute() {
        runBlocking {
            val matches = getSchedule(region, numToGet)
            if (matches.isEmpty()) {
                val message = "There are no matches to predict"
                event.channel.sendMessage(message).complete()
                return@runBlocking
            }

            matches.map { match ->
                val date = match.startTimeAsInstant()
                val msg =
                    "${longDateFormat.format(date)}: \uD83D\uDD35 **${match.blueTeamId}** vs **${match.redTeamId}** \uD83D\uDD34\n(This message will delete itself in 5 mins. DO NOT delete yourself)"
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
                    val predictions = mapOf(match.blueTeamId to blueTeamUsers, match.redTeamId to redTeamUsers)
                        .map { (team, users) ->
                            users.map { Prediction(matchId = match.matchId, userId = it, prediction = team) }
                        }.flatten().also { logger.info("Saving prediction: $it") }
                    predictions.forEach { Prediction.putItem(it) }
                    message.delete().complete()
                }
            }.forEach { it.join() }
            event.channel.sendMessage(
                "Predictions have been recorded for ${matches.size} ${if (matches.size == 1) "match" else "matches"} in the $region"
            ).complete()
        }
    }

    override fun validate() {
        validateWordCount(2..3)
        validateRegion(1)
        validateNumberPositive(2)
    }

    companion object : CommandMetadata {
        const val BLUE_TEAM_EMOJI = "\uD83D\uDD35"
        const val RED_TEAM_EMOJI = "\uD83D\uDD34"
        override val commandString: String = "predict"
        override val commandDescription: String = "Do predictions for a given league"
        override val commandData: CommandData = commandDataFromJson(
            """
            {
              "name": "$commandString",
              "type": 1,
              "description": "$commandDescription",
              "options": [
                {
                  "name": "league_id",
                  "description": "The league to query",
                  "type": 3,
                  "required": true
                },
                {
                  "name": "number_of_matches",
                  "description": "The number of matches to get",
                  "type": 3,
                  "required": false
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo): DiscordCommand = PredictCommand(event)
    }
}

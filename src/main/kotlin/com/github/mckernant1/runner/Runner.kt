package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


val BOT_TOKEN: String = System.getenv("BOT_TOKEN") ?: "NDc2MjE3ODk5OTA0OTI1Njk2.Xvj0Nw.hTwdhUgGfRNn58vtvIXRczIRiLM"

val leagueClient = LeagueClient()
val tournamentClient = TournamentClient()
val scheduleClient = ScheduleClient()

fun main() {
    JDABuilder.createDefault(BOT_TOKEN)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}


class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = event.message.contentRaw.replace("\\s+".toRegex(), " ").split(" ")

        when (words[0]) {
            "!schedule" -> scheduleCmd(words, event)
            "!info" -> printHelp(event)
//            "!predict" -> predictCmd(words, event)
        }
    }

    override fun onGenericMessageReaction(event: GenericMessageReactionEvent) {
        super.onGenericMessageReaction(event)
    }
}

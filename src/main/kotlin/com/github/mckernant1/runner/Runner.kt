package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter


const val BOT_TOKEN = "NzI1MTY5NTQ2NjMzMjgxNjI4.XvLMFw.sRJ9pTWXyxeaxAOhEoVayitk2oo"

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

        val command = words[0]
        when (command) {
            "!schedule" -> scheduleCmd(words, event)
        }
    }
}

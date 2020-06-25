package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import com.jessecorbett.diskord.dsl.bot
import com.jessecorbett.diskord.dsl.command
import com.jessecorbett.diskord.dsl.commands
import com.jessecorbett.diskord.util.words
import java.util.*


const val BOT_TOKEN = "NzI1MTY5NTQ2NjMzMjgxNjI4.XvLMFw.sRJ9pTWXyxeaxAOhEoVayitk2oo"

val leagueClient = LeagueClient()
val tournamentClient = TournamentClient()
val scheduleClient = ScheduleClient()

suspend fun main() {
    bot(BOT_TOKEN) {
        commands("!") {
            command("schedule") {
                println(words)
                if (words.size < 2) {
                    react("⛔")
                    return@command
                }
                reply(schedule(words))
            }
            command("predict") {

            }
        }
    }
}

fun schedule(words: List<String>): String {
    val getNumber = (words.getOrNull(2) ?: "3").toInt()
    val region = words[1].toUpperCase()
    val league = leagueClient.getLeagueByName(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    val schedule = scheduleClient.getSplitByTournament(league.id, tourney)
    val messageBuilder = StringBuilder()
    schedule.matches.dropWhile {
        it.date < Date()
    }.take(getNumber).forEach {
        messageBuilder.appendln("${it.team1} vs ${it.team2}")
    }
    messageBuilder.insert(0, "The next $getNumber of matches in $region are:\n")
    return messageBuilder.toString()
}

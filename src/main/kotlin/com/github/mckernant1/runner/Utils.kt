package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*


fun getSchedule(region: String, numberToGet: Int): List<Match> {
    val league = leagueClient.getLeagueByName(region)
    val tourney = tournamentClient.getMostRecentTournament(league.id)
    val schedule = scheduleClient.getSplitByTournament(league.id, tourney)
    val messageBuilder = StringBuilder()
    return schedule.matches.dropWhile {
        it.date < Date()
    }.take(numberToGet)
}


fun reactInternalError(m: Message) {
    m.addReaction("❌").complete()
}

fun reactUserError(m: Message) {
    m.addReaction("⛔").complete()
}


fun validateToRegionAndNumberOfGames(
    words: List<String>,
    event: MessageReceivedEvent
): Pair<String, Int>? {
    val message = event.message
    if (words.size < 2) {
        reactUserError(message)
        return null
    }
    return try {
        val getNumber = (words.getOrNull(2) ?: "3").toInt()
        val region = words[1].toUpperCase()
        message.addReaction("\uD83D\uDC4C").complete()
        Pair(region, getNumber)
    } catch (e: Exception) {
        reactInternalError(message)
        null
    }

}

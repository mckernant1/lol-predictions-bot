package com.github.mckernant1.runner

import com.github.mckernant1.lolapi.schedule.Match
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


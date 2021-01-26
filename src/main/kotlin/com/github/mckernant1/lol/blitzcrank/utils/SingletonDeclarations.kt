package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.blitzcrank.aws.ddb.predictions.PredictionTableAccess
import com.github.mckernant1.lol.blitzcrank.aws.ddb.predictions.PredictionsTableMock
import com.github.mckernant1.lol.blitzcrank.aws.ddb.user.UserSettingsTableAccess
import com.github.mckernant1.lol.blitzcrank.aws.ddb.user.UserSettingsTableMock
import com.github.mckernant1.lol.heimerdinger.config.EsportsApiConfig
import com.github.mckernant1.lol.heimerdinger.config.HostUrl
import com.github.mckernant1.lol.heimerdinger.leagues.LeagueClient
import com.github.mckernant1.lol.heimerdinger.schedule.ScheduleClient
import com.github.mckernant1.lol.heimerdinger.team.TeamClient
import com.github.mckernant1.lol.heimerdinger.tournaments.TournamentClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val esportsLogger: Logger = LoggerFactory.getLogger("EsportsAPIWrapper")
private val esportsApiConfig = EsportsApiConfig(
    logger = { esportsLogger.debug(it) },
    endpointHost = HostUrl.ESPORTS_API_2
)

val leagueClient = LeagueClient(esportsApiConfig = esportsApiConfig)
val tournamentClient = TournamentClient(esportsApiConfig = esportsApiConfig)
val scheduleClient = ScheduleClient(esportsApiConfig = esportsApiConfig)
val teamClient = TeamClient(esportsApiConfig = esportsApiConfig)

val predictionsTable = try {
    PredictionTableAccess()
} catch (e: ExceptionInInitializerError) {
    PredictionsTableMock()
}
val userSettingsTable = try {
    UserSettingsTableAccess()
} catch (e: ExceptionInInitializerError) {
    UserSettingsTableMock()
}


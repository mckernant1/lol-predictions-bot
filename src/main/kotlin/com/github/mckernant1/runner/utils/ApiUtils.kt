package com.github.mckernant1.runner.utils

import com.github.mckernant1.lolapi.config.EsportsApiConfig
import com.github.mckernant1.lolapi.leagues.LeagueClient
import com.github.mckernant1.lolapi.schedule.ScheduleClient
import com.github.mckernant1.lolapi.tournaments.TournamentClient
import org.apache.http.impl.client.cache.CacheConfig

private val cacheConfig: CacheConfig = CacheConfig.custom()
    .setSharedCache(false)
    .setHeuristicDefaultLifetime(7200)
    .build()

private val esportsApiConfig = EsportsApiConfig(
    cacheConfig = cacheConfig,
    logger = System.out
)

val leagueClient = LeagueClient(esportsApiConfig = esportsApiConfig)
val tournamentClient = TournamentClient(esportsApiConfig = esportsApiConfig)
val scheduleClient = ScheduleClient(esportsApiConfig = esportsApiConfig)


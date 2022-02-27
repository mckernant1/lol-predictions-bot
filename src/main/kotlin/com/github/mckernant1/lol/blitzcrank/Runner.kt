package com.github.mckernant1.lol.blitzcrank

import com.github.mckernant1.lol.blitzcrank.core.MessageListener
import com.github.mckernant1.lol.blitzcrank.timers.publishBotMetrics
import com.github.mckernant1.lol.blitzcrank.timers.reminderChecker
import com.github.mckernant1.lol.blitzcrank.utils.validateEnvironment
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.io.File

fun main() {
//    convertPredictions()
    validateEnvironment()
    val botToken: String = System.getenv("BOT_TOKEN") ?: error("BOT_TOKEN environment variable required")
    val bot = startBot(botToken)
    publishBotMetrics(bot)
    reminderChecker(bot)
}

fun startBot(token: String): JDA {
    File("store").mkdir()
    return JDABuilder.create(
        token,
        GatewayIntent.GUILD_MEMBERS,
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_MESSAGE_TYPING,
        GatewayIntent.GUILD_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGES,
        GatewayIntent.DIRECT_MESSAGE_REACTIONS,
        GatewayIntent.DIRECT_MESSAGE_TYPING
    )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.VOICE_STATE,
            CacheFlag.EMOTE,
            CacheFlag.CLIENT_STATUS
        ).setChunkingFilter(ChunkingFilter.exclude(264445053596991498))
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}


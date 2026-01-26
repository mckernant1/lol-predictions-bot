package com.mckernant1.lol.blitzcrank

import com.mckernant1.commons.assertions.Assertions.assertEnvironmentVariablesExist
import com.mckernant1.lol.blitzcrank.core.MessageListener
import com.mckernant1.lol.blitzcrank.core.commandList
import com.mckernant1.lol.blitzcrank.timers.publishBotMetrics
import com.mckernant1.lol.blitzcrank.timers.reminderChecker
import kotlinx.coroutines.future.await
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory
import org.slf4j.bridge.SLF4JBridgeHandler
import software.amazon.codeguruprofilerjavaagent.Profiler

private val logger = LoggerFactory.getLogger("MainLogger")

suspend fun main() {
    assertEnvironmentVariablesExist(
        "BOT_TOKEN",
        "ESPORTS_API_KEY",
        "PREDICTIONS_TABLE_NAME",
        "USER_SETTINGS_TABLE_NAME",
        "BOT_APPLICATION_ID"
    )
    val botToken = System.getenv("BOT_TOKEN")

    SLF4JBridgeHandler.removeHandlersForRootLogger()
    SLF4JBridgeHandler.install()
    if (false /* && System.getenv("METRICS_ENABLED").equals("true", ignoreCase = true)*/) {
        Profiler.Builder()
            .profilingGroupName("lol-predictions-bot")
            .withHeapSummary(true)
            .build()
            .start()
    }

    val bot = startBot(botToken)
    registerCommands(bot)
    publishBotMetrics(bot)
    reminderChecker(bot)
}

suspend fun startBot(token: String): JDA {
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
        .disableIntents(
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_INVITES,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.SCHEDULED_EVENTS,
            GatewayIntent.MESSAGE_CONTENT
        )
        .disableCache(
            CacheFlag.ACTIVITY,
            CacheFlag.VOICE_STATE,
            CacheFlag.EMOJI,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS
        ).setChunkingFilter(ChunkingFilter.NONE)
        .setMemberCachePolicy(MemberCachePolicy.NONE)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}

private suspend fun registerCommands(
    bot: JDA,
) {
    commandList.forEach {
        logger.info("Upserting command ${it.commandString}")
        bot.upsertCommand(it.commandData).submit().await()
    }
    logger.info("Done inserting commands!")
}

package com.mckernant1.lol.blitzcrank

import com.mckernant1.commons.assertions.Assertions.assertEnvironmentVariablesExist
import com.mckernant1.lol.blitzcrank.core.MessageListener
import com.mckernant1.lol.blitzcrank.core.commandList
import com.mckernant1.lol.blitzcrank.timers.publishBotMetrics
import com.mckernant1.lol.blitzcrank.timers.reminderChecker
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("MainLogger")

fun main() {
//    convertPredictions()
    assertEnvironmentVariablesExist(
        "BOT_TOKEN",
        "ESPORTS_API_KEY",
        "PREDICTIONS_TABLE_NAME",
        "USER_SETTINGS_TABLE_NAME",
        "BOT_APPLICATION_ID"
    )
    val botToken = System.getenv("BOT_TOKEN")
    val bot = startBot(botToken)
    registerCommands(bot)
    publishBotMetrics(bot)
    reminderChecker(bot)
}

fun startBot(token: String): JDA {
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
            CacheFlag.EMOJI,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.STICKER,
            CacheFlag.SCHEDULED_EVENTS
        ).setChunkingFilter(ChunkingFilter.exclude(264445053596991498))
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}

private fun registerCommands(
    bot: JDA
) {
    commandList.forEach {
        logger.info("Upserting command ${it.commandString}")
        bot.upsertCommand(it.commandData).complete()
    }
    logger.info("Done inserting commands!")
}

package com.github.mckernant1.lol.blitzcrank

import com.github.mckernant1.assertions.Assertions.assertEnvironmentVariablesExist
import com.github.mckernant1.lol.blitzcrank.core.MessageListener
import com.github.mckernant1.lol.blitzcrank.timers.publishBotMetrics
import com.github.mckernant1.lol.blitzcrank.timers.reminderChecker
import com.github.mckernant1.lol.blitzcrank.utils.file
import com.github.mckernant1.standalone.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.slf4j.LoggerFactory
import java.time.Duration

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
    registerCommands(botToken)
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
            CacheFlag.EMOTE,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS
        ).setChunkingFilter(ChunkingFilter.exclude(264445053596991498))
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .addEventListeners(MessageListener())
        .build()
        .awaitReady()
}

private fun registerCommands(
    botToken: String
): Unit = runBlocking {
    val botApplicationId = System.getenv("BOT_APPLICATION_ID")
    val apiUrl = "https://discord.com/api/v10/applications/$botApplicationId/commands"

    HttpClientBuilder.create()
        .build()
        .use { httpClient ->
            val files = httpClient::class.java.classLoader.getResource("commands")
                ?.file
                ?.file()
                ?.listFiles()
                ?: error("Could not load files from resources")

            files.forEach {
                delay(Duration.ofSeconds(1))
                logger.info("Creating command ${it.name}")
                val post = HttpPost(apiUrl).apply {
                    addHeader("Authorization", "Bot $botToken")
                    addHeader("Content-Type", "application/json")
                    entity = StringEntity(it.readText())
                }
                val resp = httpClient.execute(post)
                val entity = EntityUtils.toString(resp.entity)
                logger.info("Registering ${it.name} - Got response ${resp.code} $entity")
                if (resp.code != 200 && resp.code != 201) {
                    throw RuntimeException("Failed to register command: ${it.name} with $entity")
                }
            }
        }
}

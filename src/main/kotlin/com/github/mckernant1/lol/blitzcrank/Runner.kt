package com.github.mckernant1.lol.blitzcrank

import com.github.mckernant1.lol.blitzcrank.commands.*
import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.reactUserError
import com.github.mckernant1.lol.blitzcrank.utils.reactUserOk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

fun main() {
    val botToken: String = System.getenv("BOT_TOKEN") ?: throw Exception("BOT_TOKEN environment variable required")
    startBot(botToken)
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


class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = getWordsFromMessage(event.message)
        if (words.isEmpty() || event.author.id == event.jda.selfUser.id) return

        val command = when (words[0]) {
            "!schedule" -> ScheduleCommand(event)
            "!info" -> InfoCommand(event)
            "!results" -> ResultsCommand(event)
            "!standings" -> StandingsCommand(event)
            "!predict" -> PredictCommand(event)
            "!report" -> ReportCommand(event, true)
            "!predictions" -> ReportCommand(event, false)
            "!stats" -> StatsCommand(event)
            else -> return
        }

        event.channel.sendTyping()
            .queue {
                if (command.validate()) {
                    reactUserOk(event.message)
                    logger.info("Launching Coroutine...")
                    GlobalScope.launch {
                        command.execute()
                    }
                } else {
                    reactUserError(event.message)
                }
            }
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)
    }
    init {
        logger.info("MessageListener has been registered")
    }
}

package com.github.mckernant1.lol.blitzcrank

import com.github.mckernant1.lol.blitzcrank.aws.metrics.AWSCloudwatchMetricsPublisher
import com.github.mckernant1.lol.blitzcrank.aws.metrics.MetricsPublisher
import com.github.mckernant1.lol.blitzcrank.aws.metrics.NoMetricsMetricsPublisher
import com.github.mckernant1.lol.blitzcrank.commands.lol.*
import com.github.mckernant1.lol.blitzcrank.commands.util.InfoCommand
import com.github.mckernant1.lol.blitzcrank.commands.util.SetTimezoneCommand
import com.github.mckernant1.lol.blitzcrank.utils.*
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.EmbedBuilder
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
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

fun main() {
    val botToken: String = System.getenv("BOT_TOKEN") ?: error("BOT_TOKEN environment variable required")
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
            "!roster" -> RosterCommand(event)
            "!setTimezone" -> SetTimezoneCommand(event)
            else -> return
        }

        event.channel.sendTyping().complete()
        if (command.validate()) {
            reactUserOk(event.message)
            thread {
                runBlocking {
                    val commandString = "${words[0].removePrefix("!").capitalize()}Command"

                    val serverOrUser = if (event.isFromGuild) "server" else "user"

                    logger.info("Running command='${commandString}' from='${serverOrUser}' with id='${getServerIdOrUserId(event)}'")

                    cwp.putCommandUsedMetric(commandString)

                    try {
                        command.execute()
                        cwp.putNoErrorMetric()
                    } catch (e: Exception) {
                        logger.error("Caught exception while running command '$words': ", e)
                        cwp.putErrorMetric()
                        reactInternalError(event.message)
                        event.channel.sendMessage(createErrorMessage(e)).complete()
                    }
                }
            }
        } else {
            reactUserError(event.message)
        }

    }

    private fun createErrorMessage(e: Exception) = EmbedBuilder()
        .addField("Whoops!", "We have an encountered an error, you can file a github issue if you feel like it", true)
        .addField(
            "For the Developers....",
            "Timestamp: ${ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)}\n" +
                    "Error message: ${e.message}", false
        )
        .addField(
            "Useful links",
            "[Create a Github Issue](https://github.com/mckernant1/lol-predictions-bot/issues/new)\n[Join Discord Support Server](https://discord.gg/Dvq8f5KxZT)",
            false
        )
        .build()

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MessageListener::class.java)
        private val cwp: MetricsPublisher =
            if (System.getenv("METRICS_ENABLED").equals("true", ignoreCase = true))
                AWSCloudwatchMetricsPublisher()
            else
                NoMetricsMetricsPublisher()
    }

    init {
        logger.info("MessageListener has been registered")
    }
}

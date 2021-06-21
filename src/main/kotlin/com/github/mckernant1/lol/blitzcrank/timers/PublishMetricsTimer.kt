package com.github.mckernant1.lol.blitzcrank.timers

import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.globalTimer
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import java.time.Duration
import kotlin.concurrent.scheduleAtFixedRate

private val logger by lazy {
    LoggerFactory.getLogger("BotMetrics")
}

fun publishBotMetrics(bot: JDA) {
    globalTimer.scheduleAtFixedRate(0, Duration.ofMinutes(5).toMillis()) {
        runCatching {
            cwp.putNumServers(bot.guilds.size)
        }.onFailure {
            logger.error("An error occurred while posting metrics", it)
        }
    }
}

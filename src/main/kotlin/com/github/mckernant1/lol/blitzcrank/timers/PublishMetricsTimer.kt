package com.github.mckernant1.lol.blitzcrank.timers

import com.github.mckernant1.extensions.executor.scheduleAtFixedRate
import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.globalThreadPool
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import java.time.Duration

private val logger by lazy {
    LoggerFactory.getLogger("BotMetrics")
}

fun publishBotMetrics(bot: JDA) {
    globalThreadPool.scheduleAtFixedRate(Duration.ofMinutes(5)) {
        runCatching {
            cwp.putNumServers(bot.guilds.size)
        }.onFailure {
            logger.error("An error occurred while posting metrics", it)
        }
    }
}

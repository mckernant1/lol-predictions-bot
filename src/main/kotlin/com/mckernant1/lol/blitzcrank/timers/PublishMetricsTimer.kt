package com.mckernant1.lol.blitzcrank.timers

import com.mckernant1.commons.extensions.executor.Executors.scheduleAtFixedRate
import com.mckernant1.lol.blitzcrank.utils.cwp
import com.mckernant1.lol.blitzcrank.utils.periodicActionsThreadPool
import net.dv8tion.jda.api.JDA
import org.slf4j.LoggerFactory
import java.time.Duration

private val logger by lazy {
    LoggerFactory.getLogger("BotMetrics")
}

private val metrics by lazy {
    cwp.newMetrics(
        "Servers" to "Count"
    )
}

fun publishBotMetrics(bot: JDA) {
    periodicActionsThreadPool.scheduleAtFixedRate(Duration.ofMinutes(5)) {
        logger.info("Publishing metrics")
        runCatching {
            metrics.submitAndClear {
                it.addCount("count", bot.guilds.size)
            }
        }.onFailure {
            logger.error("An error occurred while posting metrics", it)
        }
    }
}

package com.mckernant1.lol.blitzcrank.timers

import com.mckernant1.commons.extensions.coroutines.Schedule.scheduleAtFixedRate
import com.mckernant1.commons.extensions.executor.Executors.scheduleAtFixedRate
import com.mckernant1.lol.blitzcrank.utils.coroutineScope
import com.mckernant1.lol.blitzcrank.utils.cwp
import com.mckernant1.lol.blitzcrank.utils.periodicActionsThreadPool
import kotlinx.coroutines.launch
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
    coroutineScope.scheduleAtFixedRate(
        Duration.ofMinutes(0),
        Duration.ofMinutes(5)
    ) {
        logger.info("Publishing metrics")
        try {
            metrics.submitAndClear {
                it.addCount("count", bot.guilds.size)
            }
        } catch (e: Exception) {
            logger.error("An error occurred while posting metrics", e)
        }
    }
}

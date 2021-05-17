package com.github.mckernant1.lol.blitzcrank.timers

import com.github.mckernant1.lol.blitzcrank.utils.cwp
import com.github.mckernant1.lol.blitzcrank.utils.globalTimer
import net.dv8tion.jda.api.JDA
import java.time.Duration
import kotlin.concurrent.scheduleAtFixedRate

fun publishBotMetrics(bot: JDA) {
    globalTimer.scheduleAtFixedRate(0, Duration.ofMinutes(5).toMillis()) {
        cwp.putNumServers(bot.guilds.size)
    }
}

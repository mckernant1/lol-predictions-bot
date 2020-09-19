package com.github.mckernant1.runner

import com.github.mckernant1.runner.commands.*
import com.github.mckernant1.runner.utils.BOT_TOKEN
import com.github.mckernant1.runner.utils.getWordsFromMessage
import com.github.mckernant1.runner.utils.reactUserError
import com.github.mckernant1.runner.utils.reactUserOk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.MemberCachePolicy
import java.io.File


fun main() {
    File("store").mkdir()
    JDABuilder.createDefault(BOT_TOKEN)
        .addEventListeners(MessageListener())
        .setChunkingFilter(ChunkingFilter.ALL)
        .setMemberCachePolicy(MemberCachePolicy.ALL)
        .enableIntents(GatewayIntent.GUILD_MEMBERS)
        .build()
        .awaitReady()
}


class MessageListener : ListenerAdapter() {
    override fun onMessageReceived(event: MessageReceivedEvent) {
        val words = getWordsFromMessage(event.message)
        if (event.author.isBot || words.isEmpty()) return

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
                    GlobalScope.launch {
                        command.execute()
                    }
                } else {
                    reactUserError(event.message)
                }
            }
    }
}

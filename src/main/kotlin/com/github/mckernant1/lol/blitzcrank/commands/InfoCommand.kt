package com.github.mckernant1.lol.blitzcrank.commands

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override suspend fun execute() {
        event.channel.sendMessage(
            infoMessage
        ).queue()
    }

    override fun validate(): Boolean = true

    companion object {

        val infoMessage = EmbedBuilder()
            .setTitle("Info")
            .addField("Basic Info", """
                !info lists this menu
                <league> refers to one of the league codes (lcs, lpl, lck, ...)
                [number of matches] is optional, picks the number of matches to display. Default is the next or previous days matches
            """.trimIndent(), false)
            .addField("Esports Commands", """
                !schedule <league> [number of matches] -> Displays the upcoming games for the region
                !results <league> [number of matches] -> Displays the most recent results for the region
                !standings <league> -> Displays the standings for the region
                !roster <team code> -> Displays the roster for a team
            """.trimIndent(), false)
            .addField("Predictions Commands", """
                !predict <league> [number of matches] -> Prints a message where you can set your predictions. Disappears after 5 mins.
                !predictions <league> [number of matches] -> Prints out he predictions for upcoming matches
                !report <league> [number of matches] -> Reports the most recent matches and who predicted what
                !stats <league> [number of matches] -> Displays the predictions standings. Default number of matches is the whole split
            """.trimIndent(), false)
            .addField("File an issue", "[Github](https://github.com/mckernant1/lol-predictions-bot/issues/new)", false)
            .addField("See Dashboard", "[AWS Dashboard](https://cloudwatch.amazonaws.com/dashboard.html?dashboard=Predictions-Bot-Dashboard&context=eyJSIjoidXMtZWFzdC0xIiwiRCI6ImN3LWRiLTY1MzUyODg3Mzk1MSIsIlUiOiJ1cy1lYXN0LTFfWWdlV3dsS0tGIiwiQyI6Ijc4OHJ1bGIzdDNvaTc3dTJjbGhoOTlzbGNpIiwiSSI6InVzLWVhc3QtMTo0ODhlOWRmNi1hOThlLTQzMTItOGE0YS0zMzZkYTVkNzI2ZWMiLCJNIjoiUHVibGljIn0=)", false)
            .build()

    }

}


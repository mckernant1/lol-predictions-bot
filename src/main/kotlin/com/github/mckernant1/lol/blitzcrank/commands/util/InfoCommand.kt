package com.github.mckernant1.lol.blitzcrank.commands.util

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
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
            .addField(
                "Basic Info", """
                !info lists this menu
                <league> refers to one of the league codes (lcs, lpl, lck, ...)
                <team code> refers to the 2 or three letter acronym for a team. example: C9 (cloud9), FPX (FunPlus Phoenix)
                [number of matches] is optional, picks the number of matches to display. Default is the next or previous days matches
            """.trimIndent(), false
            )
            .addField(
                "Esports Commands", """
                !schedule <league> [number of matches] -> Displays the upcoming games for the region
                !results <league> [number of matches] -> Displays the most recent results for the region
                !standings <league> -> Displays the standings for the region
                !roster <team code> -> Displays the roster for a team
                !record <team code> [another team code] -> displays a team's record. If a second team is provided it provides only the record against that team
            """.trimIndent(), false
            )
            .addField(
                "Predictions Commands", """
                !predict <league> [number of matches] -> Prints a message where you can set your predictions. Disappears after 5 mins.
                !predictions <league> [number of matches] -> Prints out he predictions for upcoming matches
                !report <league> [number of matches] -> Reports the most recent matches and who predicted what
                !stats <league> [number of matches] -> Displays the predictions standings. Default number of matches is the whole split
            """.trimIndent(), false
            )
            .addField(
                "User Settings",
                "!setTimezone <Timezone> -> This will set your timezone. Timezone should be formatted like America/Los_Angeles or a timezone code such as PST or CET. Example: !setTimezone PST",
                false
            )
            .addField(
                "Useful Links",
                "[File a github issue](https://github.com/mckernant1/lol-predictions-bot/issues/new)\n[Join Discord Support Server](https://discord.gg/Dvq8f5KxZT)\n[AWS Dashboard](https://cloudwatch.amazonaws.com/dashboard.html?dashboard=Predictions-Bot-Dashboard&context=eyJSIjoidXMtZWFzdC0xIiwiRCI6ImN3LWRiLTY1MzUyODg3Mzk1MSIsIlUiOiJ1cy1lYXN0LTFfWWdlV3dsS0tGIiwiQyI6Ijc4OHJ1bGIzdDNvaTc3dTJjbGhoOTlzbGNpIiwiSSI6InVzLWVhc3QtMTo0ODhlOWRmNi1hOThlLTQzMTItOGE0YS0zMzZkYTVkNzI2ZWMiLCJNIjoiUHVibGljIn0=)",
                false
            )
            .build()

    }

}


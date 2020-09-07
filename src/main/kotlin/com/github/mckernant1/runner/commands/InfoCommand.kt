package com.github.mckernant1.runner.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class InfoCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    override suspend fun execute() {
        if (!event.author.isBot) {
            event.channel.sendMessage(
                """
                !info lists this menu
                <league> refers to one of the league codes (lcs, lpl, lck, ...) 
                [number of matches] is optional, picks the number of matches to display. Default is 4
                ### Esports Commands
                !schedule <league> [number of matches] -> Displays the upcoming games for the region
                !results <league> [number of matches] -> Displays the most recent results for the region 
                !standings <league> -> Disaplys the standings for the region 
                ### Predictions Commands
                !predict <league> [number of matches] -> Prints a message where you can set your predictions. Disappears after 5 mins.
                !report <league> [number of matches] -> Reports the most recent matches and who predicted what
                !stats <league> -> Displays the predictions standings
            """.trimIndent()
            ).queue()
        }
    }

    override fun validate(): Boolean = true

}


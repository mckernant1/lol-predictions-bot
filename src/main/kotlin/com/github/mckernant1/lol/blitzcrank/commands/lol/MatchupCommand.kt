package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class MatchupCommand(event: MessageReceivedEvent): DiscordCommand(event) {

    override suspend fun execute(){

    }
    /*
    !matchup <team1> <team2>
     */
    override fun validate(): Boolean =
        validateWordCount(event, 3..3)
            && validateTeam(event, 1)
                && validateTeam(event, 2);

}

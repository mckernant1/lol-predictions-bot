package com.github.mckernant1.lol.blitzcrank.commands

import com.github.mckernant1.lol.blitzcrank.utils.getWordsFromMessage
import com.github.mckernant1.lol.blitzcrank.utils.teamClient
import com.github.mckernant1.lol.heimerdinger.team.Team
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RosterCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2)
    }


    override suspend fun execute() {
        val teamToGet = getWordsFromMessage(event.message)[1]
        val team = teamClient.getTeamByCode(teamToGet)

        val message = formatMessage(team)

        event.channel.sendMessage(message).complete()
    }

    private fun formatMessage(team: Team): String {
        return "${team.name.toUpperCase()} Current Roster:\n" +
                team.players
                    .sortedBy { RoleSort.valueOf(it.role.toUpperCase()).sorter }
                    .groupBy { it.role }
                    .map { role -> "${role.key.capitalize()}:\n" +
                            role.value.joinToString("\n") { " -${it.summonerName}" }
                    }.joinToString("\n") { it }
    }

    private enum class RoleSort(val sorter: Int) {
        TOP(1),
        JUNGLE(2),
        MID(3),
        BOTTOM(4),
        SUPPORT(5)
    }

}

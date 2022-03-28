package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.extensions.strings.capitalize
import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import com.github.mckernant1.lol.esports.api.Team
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class RosterCommand(event: MessageReceivedEvent) : DiscordCommand(event) {

    override fun validate() {
        validateWordCount(2..2)
        validateTeam(1)
    }


    override suspend fun execute() {
        val teamToGet = words[1]
        val team = apiClient.getTeamByCode(teamToGet.uppercase())

        val message = formatMessage(team)

        event.channel.sendMessage(message).complete()
    }

    private fun formatMessage(team: Team): String {
        return "${team.name.capitalize()} Current Roster:\n" +
                apiClient.getPlayersOnTeam(team.teamId.uppercase())
                    .asSequence()
                    .filter { player -> RoleSort.values().any { it.name.equals(player.role, true) } }
                    .sortedBy { RoleSort.valueOf(it.role!!.uppercase()).sorter }
                    .groupBy { it.role }
                    .map { role ->
                        "${role.key!!.capitalize()}:\n" +
                                role.value.joinToString("\n") { " -${it.id}" }
                    }.joinToString("\n") { it }
    }

    private enum class RoleSort(val sorter: Int) {
        TOP(1),
        JUNGLE(2),
        MID(3),
        BOT(4),
        SUPPORT(5),
        COACH(6),
        NONE(7);
    }

}

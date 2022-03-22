package com.github.mckernant1.lol.blitzcrank.commands.lol

import com.github.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.github.mckernant1.lol.blitzcrank.utils.apiClient
import com.github.mckernant1.lol.esports.api.Team
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import java.util.*

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
        return "${team.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }} Current Roster:\n" +
                apiClient.playersTeamIdGet(team.teamId.uppercase())
                    .asSequence()
                    .filter { player -> RoleSort.values().any { it.name.equals(player.role, true) } }
                    .sortedBy { RoleSort.valueOf(it.role!!.uppercase()).sorter }
                    .groupBy { it.role }
                    .map { role ->
                        "${role.key!!.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}:\n" +
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

package com.mckernant1.lol.blitzcrank.commands.lol

import com.mckernant1.commons.extensions.strings.capitalize
import com.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.mckernant1.lol.blitzcrank.commands.DiscordCommand
import com.mckernant1.lol.blitzcrank.model.CommandInfo
import com.mckernant1.lol.blitzcrank.model.UserSettings
import com.mckernant1.lol.blitzcrank.utils.apiClient
import com.mckernant1.lol.blitzcrank.utils.commandDataFromJson
import com.mckernant1.lol.esports.api.models.Team
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import net.dv8tion.jda.api.interactions.commands.build.CommandData

class RosterCommand(event: CommandInfo, userSettings: UserSettings) : DiscordCommand(event, userSettings) {
    override suspend fun validate(options: Map<String, String>) {
        validateTeam(options["team_id"])
    }


    override suspend fun execute() {
        val teamToGet = event.options["team_id"]!!
        val team = withContext(Dispatchers.IO) {
            apiClient.getTeamByCode(teamToGet.uppercase())
        }

        val message = formatMessage(team)

        event.channel.sendMessage(message).submit().await()
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

    companion object : CommandMetadata {
        override val commandString: String = "roster"
        override val commandDescription: String = "Get the roster for the given team"
        override val commandData: CommandData = commandDataFromJson(
            """
            {
              "name": "$commandString",
              "type": 1,
              "description": "$commandDescription",
              "options": [
                {
                  "name": "team_id",
                  "description": "The team to query",
                  "type": 3,
                  "required": true
                }
              ]
            }
        """.trimIndent()
        )

        override fun create(event: CommandInfo, userSettings: UserSettings): DiscordCommand = RosterCommand(event, userSettings)
    }

}

package com.github.mckernant1.lol.blitzcrank.register

import net.dv8tion.jda.api.interactions.commands.build.CommandData

private val addReminderCommandData: CommandData = commandDataFromJson("""
    {
      "name": "add-reminder",
      "type": 1,
      "description": "Add a reminder about upcoming games",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "hours_before",
          "description": "The number of hours before to remind",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

private val deleteReminderCommandData: CommandData = commandDataFromJson("""
    {
      "name": "delete-reminder",
      "type": 1,
      "description": "Delete a reminder about upcoming games",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "hours_before",
          "description": "The number of hours before to remind",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

private val listReminderCommandData: CommandData = commandDataFromJson("""
    {
      "name": "list-reminders",
      "type": 1,
      "description": "List your reminders"
    }
""".trimIndent())

private val setTimezoneCommandData: CommandData = commandDataFromJson("""
    {
      "name": "set-timezone",
      "type": 1,
      "description": "Get the roster for the given team",
      "options": [
        {
          "name": "timezone",
          "description": "The timezone",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

internal val settingsCommands: List<CommandData> = listOf(
    addReminderCommandData,
    deleteReminderCommandData,
    listReminderCommandData,
    setTimezoneCommandData
)

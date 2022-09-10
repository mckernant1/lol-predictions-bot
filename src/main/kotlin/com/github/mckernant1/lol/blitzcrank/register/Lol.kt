package com.github.mckernant1.lol.blitzcrank.register

import net.dv8tion.jda.api.interactions.commands.build.CommandData



private val ongoingCommandData: CommandData = commandDataFromJson("""
    {
      "name": "ongoing",
      "type": 1,
      "description": "Lists the ongoing tournaments"
    }
""".trimIndent())

private val predictCommandData: CommandData = commandDataFromJson("""
    {
      "name": "predict",
      "type": 1,
      "description": "Do predictions for a given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "number_of_matches",
          "description": "The number of matches to get",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val predictionsCommandData: CommandData = commandDataFromJson("""
    {
      "name": "predictions",
      "type": 1,
      "description": "The predictions for the given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "number_of_matches",
          "description": "The number of matches to get",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val recordCommandData: CommandData = commandDataFromJson("""
    {
      "name": "record",
      "type": 1,
      "description": "Get the record of a team in a given tournament",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "team1",
          "description": "The team who's record to get",
          "type": 3,
          "required": true
        },
        {
          "name": "team2",
          "description": "The team to compare against",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val reportCommandData: CommandData = commandDataFromJson("""
    {
      "name": "report",
      "type": 1,
      "description": "Get a report on predictions for a given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "number_of_matches",
          "description": "The number of matches to get",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val resultCommandData: CommandData = commandDataFromJson("""
    {
      "name": "results",
      "type": 1,
      "description": "The results for the given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "number_of_matches",
          "description": "The number of matches to get",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val rosterCommandData: CommandData = commandDataFromJson("""
    {
      "name": "roster",
      "type": 1,
      "description": "Get the roster for the given team",
      "options": [
        {
          "name": "team_id",
          "description": "The team to query",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

private val scheduleCommandData: CommandData = commandDataFromJson("""
    {
      "name": "schedule",
      "type": 1,
      "description": "Get Schedules for the given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        },
        {
          "name": "number_of_matches",
          "description": "The number of matches to get",
          "type": 3,
          "required": false
        }
      ]
    }
""".trimIndent())

private val standingsCommandData: CommandData = commandDataFromJson("""
    {
      "name": "standings",
      "type": 1,
      "description": "The standings in the given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

private val statsCommandData: CommandData = commandDataFromJson("""
    {
      "name": "stats",
      "type": 1,
      "description": "The stats for the given league",
      "options": [
        {
          "name": "league_id",
          "description": "The league to query",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

internal val lolCommands: Set<CommandData> = setOf(
    statsCommandData,
    standingsCommandData,
    scheduleCommandData,
    ongoingCommandData,
    rosterCommandData,
    resultCommandData,
    reportCommandData,
    recordCommandData,
    predictCommandData,
    predictionsCommandData
)

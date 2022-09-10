package com.github.mckernant1.lol.blitzcrank.register

import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.utils.data.DataObject


private val infoCommandData: CommandData = CommandData.fromData(DataObject.fromJson("""
    {
      "name": "info",
      "type": 1,
      "description": "Get the bot info",
      "options": [
        {
          "name": "help",
          "description": "The more help",
          "type": 3,
          "required": false,
          "choices": [
            {
              "name": "esports",
              "value": "esports"
            },
            {
              "name": "predict",
              "value": "predict"
            },
            {
              "name": "settings",
              "value": "settings"
            }
          ]
        }
      ]
    }
""".trimIndent()))


internal val infoCommands: List<CommandData> = listOf(
    infoCommandData
)

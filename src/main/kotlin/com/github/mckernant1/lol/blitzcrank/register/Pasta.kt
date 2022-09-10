package com.github.mckernant1.lol.blitzcrank.register

import net.dv8tion.jda.api.interactions.commands.build.CommandData


private val pastaCommandData: CommandData = commandDataFromJson("""
    {
      "name": "pasta",
      "type": 1,
      "description": "Pastes your pasta"
    }

""".trimIndent())

private val setPastaCommandData: CommandData = commandDataFromJson("""
    {
      "name": "set-pasta",
      "type": 1,
      "description": "Set your pasta",
      "options": [
        {
          "name": "pasta",
          "description": "The pasta you want to save",
          "type": 3,
          "required": true
        }
      ]
    }
""".trimIndent())

internal val pastaCommands: List<CommandData> = listOf(
    pastaCommandData,
    setPastaCommandData
)

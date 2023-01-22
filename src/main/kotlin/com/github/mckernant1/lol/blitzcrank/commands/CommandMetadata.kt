package com.github.mckernant1.lol.blitzcrank.commands

import com.github.mckernant1.lol.blitzcrank.model.CommandInfo
import net.dv8tion.jda.api.interactions.commands.build.CommandData

interface CommandMetadata {

    val commandString: String
    val commandDescription: String
    val commandData: CommandData

    fun create(event: CommandInfo): DiscordCommand

}

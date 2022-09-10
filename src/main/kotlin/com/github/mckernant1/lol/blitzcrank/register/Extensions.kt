package com.github.mckernant1.lol.blitzcrank.register

import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.utils.data.DataObject


internal fun commandDataFromJson(json: String): CommandData = CommandData.fromData(DataObject.fromJson(json))

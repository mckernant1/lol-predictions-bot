package com.mckernant1.lol.blitzcrank.utils

import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.utils.data.DataObject
import java.io.File

fun String.file(): File = File(this)

internal fun commandDataFromJson(json: String): CommandData = CommandData.fromData(DataObject.fromJson(json))

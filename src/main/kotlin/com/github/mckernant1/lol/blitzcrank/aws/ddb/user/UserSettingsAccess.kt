package com.github.mckernant1.lol.blitzcrank.aws.ddb.user

import com.github.mckernant1.lol.blitzcrank.model.UserSettings

interface UserSettingsAccess {

    fun getSettingsForUser(discordId: String): UserSettings

    fun putSettings(settings: UserSettings)
}

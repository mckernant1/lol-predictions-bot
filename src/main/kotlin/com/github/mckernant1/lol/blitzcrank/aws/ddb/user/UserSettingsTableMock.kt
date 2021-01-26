package com.github.mckernant1.lol.blitzcrank.aws.ddb.user

import com.github.mckernant1.lol.blitzcrank.model.UserSettings

class UserSettingsTableMock : UserSettingsAccess {
    override fun getSettingsForUser(discordId: String): UserSettings = UserSettings(discordId = discordId)

    override fun putSettings(settings: UserSettings) = Unit
}

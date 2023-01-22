package com.github.mckernant1.lol.blitzcrank.utils.model

import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.entities.User

data class BotUser(
    val discordUser: User,
    val settings: UserSettings
) {
    fun getId() = discordUser.id

    fun getMentionable(): String {
        return if (settings.notifyMe) {
            "<@${getId()}>"
        } else {
            "@${this.discordUser.name}"
        }
    }
}

package com.mckernant1.lol.blitzcrank.utils.model

import com.mckernant1.lol.blitzcrank.model.UserSettings
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

data class BotUser(
    val discordId: String,
    val nickname: String,
    val settings: UserSettings
) {
    constructor(user: User, settings: UserSettings) : this(user.id, user.name, settings)

    constructor(member: Member, settings: UserSettings) : this(member.id, member.effectiveName, settings)

    fun getId() = discordId

    fun getMentionable(): String {
        return if (settings.notifyMe) {
            "<@${getId()}>"
        } else {
            "@${this.nickname}"
        }
    }
}

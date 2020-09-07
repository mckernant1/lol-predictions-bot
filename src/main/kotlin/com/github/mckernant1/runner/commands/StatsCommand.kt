package com.github.mckernant1.runner.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class StatsCommand(event: MessageReceivedEvent) : MongoCommand(event) {
    override suspend fun execute() {
        
    }

    override fun validate(): Boolean {
        return validateWordCount(event, 2..2) && validateRegion(event, 1)
    }
}

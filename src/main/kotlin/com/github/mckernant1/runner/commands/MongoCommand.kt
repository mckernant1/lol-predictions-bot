package com.github.mckernant1.runner.commands

import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.litote.kmongo.KMongo
import org.litote.kmongo.getCollection

abstract class MongoCommand(event: MessageReceivedEvent) : DiscordCommand(event) {
    private val client = KMongo.createClient("mongodb://$dbUser:$dbPassword@ds359118.mlab.com:59118/lol-prediction?retryWrites=false")
    protected val collection = client.getDatabase("lol-prediction").getCollection<Prediction>("discord-predictions")

    protected data class Prediction (
        val matchId: String,
        val userId: String,
        val prediction: String
    )

    companion object {
        private val dbUser: String = System.getenv("MONGO_USER") ?: throw Exception("Environment variable MONGO_USER has not been specified")
        private val dbPassword: String = System.getenv("MONGO_PASSWORD") ?: throw Exception("Environment variable MONGO_PASSWORD has not been specified")
    }
}

package com.github.mckernant1.lol.blitzcrank.integration

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.TextChannel
import org.testng.Assert
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import java.time.Duration

internal open class TestBase {
    companion object {
        const val testTimeoutMillis = 120000L
    }

    protected lateinit var testerBot: JDA
    protected lateinit var testerBotChannel: TextChannel

    @BeforeClass(groups = ["integration"])
    fun setup() {
        val testToken = System.getenv("TEST_BOT_TOKEN")
            ?: throw Exception("The TEST_BOT_TOKEN has not been initialized")
        testerBot = JDABuilder.createDefault(testToken)
            .build()
            .awaitReady()
        val testerBotGuild = testerBot.guilds.find { it.idLong == 725179342631600158 }!!
        testerBotChannel = testerBotGuild.textChannels.find { it.name == "beta-testing" }!!
        testerBotChannel.sendMessage("TesterBot Ready").complete()
    }

    @AfterClass(groups = ["integration"])
    fun shutdown() {
        testerBot.shutdownNow()
    }


    fun runGenericTest(commandString: String) = runBlocking {
        val message = testerBotChannel.sendMessage(commandString).complete()
        delay(Duration.ofSeconds(10).toMillis())
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == commandString) {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        Assert.assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
        Assert.assertEquals(message.retrieveReactionUsers("❌").complete().size, 0)
        Assert.assertEquals(message.retrieveReactionUsers("⛔").complete().size, 0)
    }

}

package com.github.mckernant1.lol.blitzcrank.integration

import com.github.mckernant1.lol.blitzcrank.startBot
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.TextChannel
import org.testng.Assert.assertEquals
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass
import org.testng.annotations.Test

class ReactOkTest {

    companion object {
        private const val testTimeoutMillis = 120000L
    }

    private lateinit var betaBot: JDA
    private lateinit var testerBot: JDA
    private lateinit var betaBotChannel: TextChannel
    private lateinit var testerBotChannel: TextChannel

    @BeforeClass(groups = ["integration"])
    fun setup() {
        val testToken = System.getenv("TEST_BOT_TOKEN")
            ?: throw Exception("The TEST_BOT_TOKEN has not been initialized")
        val betaBotToken = System.getenv("BETA_BOT_TOKEN")
            ?: throw Exception("The BETA_BOT_TOKEN has not been initialized")
        betaBot = startBot(betaBotToken)
        testerBot = JDABuilder.createDefault(testToken)
            .build()
            .awaitReady()
        val betaBotGuild = betaBot.guilds.find { it.idLong == 725179342631600158 }!!
        val testerBotGuild = testerBot.guilds.find { it.idLong == 725179342631600158 }!!
        betaBotChannel = betaBotGuild.textChannels.find { it.name == "beta-testing" }!!
        testerBotChannel = testerBotGuild.textChannels.find { it.name == "beta-testing" }!!

        betaBotChannel.sendMessage("BetaBot Ready").complete()
        testerBotChannel.sendMessage("TesterBot Ready").complete()
    }

    @AfterClass(groups = ["integration"])
    fun shutdown() {
        testerBot.shutdownNow()
        betaBot.shutdownNow()
    }


    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkInfoCommand() {
        val message = testerBotChannel.sendMessage("!info").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentDisplay
        while (mostRecentMessage == "!info") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentDisplay
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }

    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkResultsOk() {
        val message = testerBotChannel.sendMessage("!results worlds").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!results worlds") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }

    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkWorldsScheduleOk() {
        val message = testerBotChannel.sendMessage("!schedule worlds").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!schedule worlds") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }

    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkLCSScheduleOk() {
        val message = testerBotChannel.sendMessage("!schedule worlds").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!schedule lcs") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }

    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkLCKScheduleOk() {
        val message = testerBotChannel.sendMessage("!schedule worlds").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!schedule lck") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }
}

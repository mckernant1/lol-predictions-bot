package com.github.mckernant1.lol.blitzcrank.integration

import com.github.mckernant1.lol.blitzcrank.startBot
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.entities.TextChannel
import org.testng.annotations.AfterClass
import org.testng.annotations.BeforeClass

internal open class TestBase {
    companion object {
        const val testTimeoutMillis = 120000L
    }

    protected lateinit var betaBot: JDA
    protected lateinit var testerBot: JDA
    protected lateinit var betaBotChannel: TextChannel
    protected lateinit var testerBotChannel: TextChannel

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
}

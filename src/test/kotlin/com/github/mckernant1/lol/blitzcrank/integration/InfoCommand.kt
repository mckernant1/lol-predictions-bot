package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

internal class InfoCommand : TestBase() {

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

}

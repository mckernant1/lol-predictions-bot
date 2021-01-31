package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.Assert.assertEquals
import org.testng.annotations.Test

internal class WorldsResults : TestBase() {
    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkResultsOk() {
        val message = testerBotChannel.sendMessage("!results worlds").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!results worlds") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
        assertEquals(message.retrieveReactionUsers("❌").complete().size, 0)
    }
}

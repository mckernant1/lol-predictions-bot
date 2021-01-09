package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.Assert
import org.testng.annotations.Test

internal class LCKSchedule : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCKScheduleOk() {
        val message = testerBotChannel.sendMessage("!schedule lck").complete()
        var mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        while (mostRecentMessage == "!schedule lck") {
            println("Waiting for responce")
            mostRecentMessage = testerBotChannel.history.retrievePast(1).complete().first().contentRaw
        }
        Assert.assertEquals(message.retrieveReactionUsers("\uD83D\uDC4C").complete().size, 1)
    }
}

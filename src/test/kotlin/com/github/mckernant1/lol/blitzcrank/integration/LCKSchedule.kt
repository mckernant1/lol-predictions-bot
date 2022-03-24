package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class LCKSchedule : TestBase() {

    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCKScheduleOk() {
        runGenericTest("!schedule lck")
    }
}

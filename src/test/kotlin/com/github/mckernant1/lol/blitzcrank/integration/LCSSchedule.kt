package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class LCSSchedule : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCSScheduleOk() {
        runGenericTest("!schedule lcs")
    }
}

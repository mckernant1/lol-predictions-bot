package com.github.mckernant1.lol.blitzcrank.integration

internal class LCKSchedule : TestBase() {
    // LCK API is broken right now
//    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCKScheduleOk() {
        runGenericTest("!schedule lck")
    }
}

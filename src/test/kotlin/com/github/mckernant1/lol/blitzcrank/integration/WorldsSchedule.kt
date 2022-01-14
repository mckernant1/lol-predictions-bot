package com.github.mckernant1.lol.blitzcrank.integration

internal class WorldsSchedule : TestBase() {
    // Disable worlds for now
//    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkWorldsScheduleOk() {
        runGenericTest("!schedule worlds")
    }
}

package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class WorldsSchedule : TestBase() {
    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkWorldsScheduleOk() {
        runGenericTest("/schedule league_id: wcs")
    }
}

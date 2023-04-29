package com.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class LCSStats : TestBase() {

    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCSStats() {
        runGenericTest("/stats league_id: lcs")
    }

}

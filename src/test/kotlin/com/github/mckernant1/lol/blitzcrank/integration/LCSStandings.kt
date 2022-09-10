package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class LCSStandings : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCSStandings() {
        runGenericTest("/standings league_id: lcs")
    }
}

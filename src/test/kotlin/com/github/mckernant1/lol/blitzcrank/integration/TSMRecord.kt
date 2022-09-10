package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class TSMRecord : TestBase() {

    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkTSMRecord() {
        runGenericTest("/record league_id: lcs team_id: tsm")
    }
}

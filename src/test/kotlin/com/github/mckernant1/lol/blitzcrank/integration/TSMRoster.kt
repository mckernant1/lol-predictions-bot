package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class TSMRoster : TestBase() {

    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkTSMRosterOk() {
        runGenericTest("!roster tsm")
    }
}

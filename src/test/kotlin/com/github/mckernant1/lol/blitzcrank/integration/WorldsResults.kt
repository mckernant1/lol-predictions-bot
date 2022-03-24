package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class WorldsResults : TestBase() {
    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkResultsOk() {
        runGenericTest("!results worlds")
    }
}

package com.github.mckernant1.lol.blitzcrank.integration

internal class WorldsResults : TestBase() {
    // Disable Worlds for now while its broken
//    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkResultsOk() {
        runGenericTest("!results worlds")
    }
}

package com.github.mckernant1.lol.blitzcrank.integration

internal class LCSStandings : TestBase() {
// Standings API seems to be broken...
//    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCSStandings() {
        runGenericTest("!standings lcs")
    }
}

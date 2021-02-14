package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class SetTimezone : TestBase() {

    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkTimezone() {
        runGenericTest("!setTimezone GMT-8")
    }

}

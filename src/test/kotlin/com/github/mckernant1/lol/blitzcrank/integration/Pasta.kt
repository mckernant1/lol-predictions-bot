package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class Pasta : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkPasta() {
        runGenericTest("!pasta")
    }
}

package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class SetPasta : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkPasta() {
        runGenericTest("!setPasta I am a bot. beep boop")
    }
}

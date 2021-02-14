package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class InfoCommand : TestBase() {

    @Test(groups = ["integration"], timeOut = testTimeoutMillis)
    fun checkInfoCommand() {
        runGenericTest("!info")
    }

}

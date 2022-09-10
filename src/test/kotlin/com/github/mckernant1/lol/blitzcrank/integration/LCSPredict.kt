package com.github.mckernant1.lol.blitzcrank.integration

import org.testng.annotations.Test

internal class LCSPredict : TestBase() {
    @Test(groups = ["integration"], timeOut = TestBase.testTimeoutMillis)
    fun checkLCSPredictOk() {
        runGenericTest("/predict league_id: lcs")
    }
}

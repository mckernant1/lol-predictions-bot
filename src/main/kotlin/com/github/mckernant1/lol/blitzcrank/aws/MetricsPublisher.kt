package com.github.mckernant1.lol.blitzcrank.aws

interface MetricsPublisher {

    fun putCommandUsedMetric(commandName: String)

    fun putErrorMetric()
}

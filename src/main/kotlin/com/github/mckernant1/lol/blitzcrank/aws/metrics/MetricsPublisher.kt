package com.github.mckernant1.lol.blitzcrank.aws.metrics

interface MetricsPublisher {

    fun putCommandUsedMetric(commandName: String)

    fun putErrorMetric()
}

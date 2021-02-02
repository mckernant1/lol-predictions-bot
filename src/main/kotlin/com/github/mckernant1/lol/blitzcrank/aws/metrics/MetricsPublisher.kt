package com.github.mckernant1.lol.blitzcrank.aws.metrics

interface MetricsPublisher {

    fun putNumServers(number: Int)

    fun putCommandUsedMetric(commandName: String)

    fun putErrorMetric()

    fun putNoErrorMetric()
}

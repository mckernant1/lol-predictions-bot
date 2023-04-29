package com.mckernant1.lol.blitzcrank.metrics

interface MetricsPublisher {

    fun putNumServers(number: Int)

    fun putCommandUsedMetric(commandName: String)

    fun putErrorMetric()

    fun putNoErrorMetric()
}

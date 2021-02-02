package com.github.mckernant1.lol.blitzcrank.aws.metrics

class NoMetricsMetricsPublisher : MetricsPublisher {
    override fun putNumServers(number: Int) = Unit

    override fun putCommandUsedMetric(commandName: String) = Unit

    override fun putErrorMetric() = Unit

    override fun putNoErrorMetric() = Unit
}

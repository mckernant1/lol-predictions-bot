package com.mckernant1.lol.blitzcrank.metrics

@Deprecated("Use metrics library instead")
class NoMetricsMetricsPublisher : MetricsPublisher {
    override fun putNumServers(number: Int) = Unit

    override fun putCommandUsedMetric(commandName: String) = Unit

    override fun putErrorMetric() = Unit

    override fun putNoErrorMetric() = Unit
}

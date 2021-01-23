package com.github.mckernant1.lol.blitzcrank.aws

class NoMetricsMetricsPublisher : MetricsPublisher {
    override fun putCommandUsedMetric(commandName: String) = Unit

    override fun putErrorMetric() = Unit
}

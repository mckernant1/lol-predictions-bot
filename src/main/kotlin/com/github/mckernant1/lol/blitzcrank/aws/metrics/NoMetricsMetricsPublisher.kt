package com.github.mckernant1.lol.blitzcrank.aws.metrics

class NoMetricsMetricsPublisher : MetricsPublisher {
    override fun putCommandUsedMetric(commandName: String) = Unit

    override fun putErrorMetric() = Unit
}
package com.mckernant1.lol.blitzcrank.metrics

import software.amazon.awssdk.services.cloudwatch.model.Dimension
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Deprecated("Use metrics library instead")
class AWSCloudwatchMetricsPublisher : MetricsPublisher {

    companion object {
        private const val NAMESPACE = "Discord-bots/Predictions-Bot"
    }

    override fun putNumServers(number: Int) {
        val dimension = Dimension.builder()
            .name("Servers")
            .value("Count")
            .build()

        val instant = getInstant()

        val datum = MetricDatum.builder()
            .metricName("count")
            .unit(StandardUnit.COUNT)
            .value(number.toDouble())
            .timestamp(instant)
            .dimensions(dimension).build()

        PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        // cw.putMetricData(request)
    }

    override fun putCommandUsedMetric(commandName: String) {
        val dimension = Dimension.builder()
            .name("Commands")
            .value("Count")
            .build()

        val instant = getInstant()

        val datum = MetricDatum.builder()
            .metricName(commandName)
            .unit(StandardUnit.COUNT)
            .value(1.0)
            .timestamp(instant)
            .dimensions(dimension).build()

        PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        // cw.putMetricData(request)
    }

    override fun putErrorMetric() {
        val dimension = Dimension.builder()
            .name("ErrorCount")
            .value("Count")
            .build()

        val instant = getInstant()

        val datum = MetricDatum.builder()
            .metricName("Error")
            .unit(StandardUnit.COUNT)
            .value(1.0)
            .timestamp(instant)
            .dimensions(dimension).build()

        PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        // cw.putMetricData(request)
    }

    override fun putNoErrorMetric() {
        val dimension = Dimension.builder()
            .name("ErrorCount")
            .value("Count")
            .build()

        val instant = getInstant()

        val datum = MetricDatum.builder()
            .metricName("Error")
            .unit(StandardUnit.COUNT)
            .value(0.0)
            .timestamp(instant)
            .dimensions(dimension).build()

        PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        // cw.putMetricData(request)
    }

    private fun getInstant() = Instant.parse(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))

}

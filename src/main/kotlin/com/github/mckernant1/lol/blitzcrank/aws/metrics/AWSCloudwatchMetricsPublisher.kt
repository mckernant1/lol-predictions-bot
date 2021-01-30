package com.github.mckernant1.lol.blitzcrank.aws.metrics

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient
import software.amazon.awssdk.services.cloudwatch.model.Dimension
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class AWSCloudwatchMetricsPublisher : MetricsPublisher {

    private val cw = CloudWatchClient.builder().build()

    companion object {
        private const val NAMESPACE = "Discord-bots/Predictions-Bot"
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

        val request = PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        cw.putMetricData(request)
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

        val request = PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        cw.putMetricData(request)
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

        val request = PutMetricDataRequest.builder()
            .namespace(NAMESPACE)
            .metricData(datum).build()
        cw.putMetricData(request)
    }

    private fun getInstant() = Instant.parse(ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT))

}

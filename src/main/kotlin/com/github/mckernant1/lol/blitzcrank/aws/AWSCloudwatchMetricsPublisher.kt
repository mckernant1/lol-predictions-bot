package com.github.mckernant1.lol.blitzcrank.aws

import software.amazon.awssdk.services.cloudwatch.CloudWatchClient
import software.amazon.awssdk.services.cloudwatch.model.Dimension
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit
import java.time.Instant
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class AWSCloudwatchMetricsPublisher {

    private val cw = CloudWatchClient.builder().build()

    fun putCommandUsed(commandName: String) {
        val dimension = Dimension.builder()
            .name("COMMANDS")
            .value("COUNT")
            .build()

        val time = ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_INSTANT)
        val instant = Instant.parse(time)

        val datum = MetricDatum.builder()
            .metricName(commandName)
            .unit(StandardUnit.COUNT)
            .value(1.0)
            .timestamp(instant)
            .dimensions(dimension).build()

        val request = PutMetricDataRequest.builder()
            .namespace("DISCORD-BOTS/PREDICTIONS-BOT")
            .metricData(datum).build()


        cw.putMetricData(request)
    }

}

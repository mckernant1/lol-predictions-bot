package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.blitzcrank.metrics.AWSCloudwatchMetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.MetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.NoMetricsMetricsPublisher
import com.github.mckernant1.lol.esports.ApiClient
import org.openapitools.client.api.DefaultApi
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.util.Timer

internal val dc by lazy { DynamoDbClient.builder().build() }
internal val ddbClient: DynamoDbEnhancedClient by lazy {
    DynamoDbEnhancedClient.builder()
        .dynamoDbClient(dc)
        .build()
}

internal val cwp: MetricsPublisher by lazy {
    if (System.getenv("METRICS_ENABLED").equals("true", ignoreCase = true))
        AWSCloudwatchMetricsPublisher()
    else
        NoMetricsMetricsPublisher()
}

internal val globalTimer by lazy { Timer() }

internal val apiClient by lazy {
    DefaultApi(ApiClient().apply {
        setApiKey("CO6gm83RDj3U7LW2uFqKx41n0S834zFi4V7o2fKL")
    })
}


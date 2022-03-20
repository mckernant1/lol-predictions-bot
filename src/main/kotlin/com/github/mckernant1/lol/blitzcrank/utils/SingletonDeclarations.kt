package com.github.mckernant1.lol.blitzcrank.utils

import com.github.mckernant1.lol.blitzcrank.metrics.AWSCloudwatchMetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.MetricsPublisher
import com.github.mckernant1.lol.blitzcrank.metrics.NoMetricsMetricsPublisher
import com.github.mckernant1.lol.esports.ApiClient
import okhttp3.OkHttpClient
import org.openapitools.client.api.DefaultApi
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.time.Duration
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

internal val apiClient = DefaultApi(
    ApiClient(
        OkHttpClient.Builder()
            .readTimeout(Duration.ofSeconds(60))
            .connectTimeout(Duration.ofSeconds(60))
            .writeTimeout(Duration.ofSeconds(60))
            .build()
    ).apply {
        setApiKey(System.getenv("ESPORTS_API_KEY")
            ?: error("ESPORTS_API_KEY environment variable is missing"))
    }
)

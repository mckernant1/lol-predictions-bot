package com.mckernant1.lol.blitzcrank.utils

import com.mckernant1.lol.blitzcrank.metrics.AWSCloudwatchMetricsPublisher
import com.mckernant1.lol.blitzcrank.metrics.MetricsPublisher
import com.mckernant1.lol.blitzcrank.metrics.NoMetricsMetricsPublisher
import com.mckernant1.lol.esports.api.ApiClient
import com.mckernant1.lol.esports.api.client.DefaultApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.io.File
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledThreadPoolExecutor

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

internal val globalThreadPool: ScheduledExecutorService by lazy {
    ScheduledThreadPoolExecutor(10)
}

internal val apiClient = DefaultApi(
    ApiClient(
        OkHttpClient.Builder()
            .readTimeout(Duration.ofSeconds(60))
            .connectTimeout(Duration.ofSeconds(60))
            .writeTimeout(Duration.ofSeconds(60))
            .cache(
                Cache(
                    File("store"),
                    50L * 1024L * 1024L // 50 MiB
                )
            )
            .build()
    ).apply {
        basePath = "https://v2-api.lol-esports.mckernant1.com"
    }
)

package com.mckernant1.lol.blitzcrank.utils

import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.mckernant1.commons.metrics.Metrics
import com.mckernant1.commons.metrics.impls.CloudWatchMetrics
import com.mckernant1.commons.metrics.impls.NoopMetrics
import com.mckernant1.lol.esports.api.ApiClient
import com.mckernant1.lol.esports.api.client.DefaultApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Cache
import okhttp3.OkHttpClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.io.File
import java.time.Duration
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

internal val dc by lazy { DynamoDbAsyncClient.builder().build() }
internal val ddbClient: DynamoDbEnhancedAsyncClient by lazy {
    DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(dc)
        .build()
}

internal val cloudWatchClient: CloudWatchAsyncClient by lazy {
    CloudWatchAsyncClient.create()
}

private const val NAMESPACE = "Discord-bots/Predictions-Bot"

internal val cwp: Metrics by lazy {
    if (System.getenv("METRICS_ENABLED").equals("true", ignoreCase = true)) {
        CloudWatchMetrics(NAMESPACE, cloudWatchClient)
    } else {
        NoopMetrics()
    }
}

internal val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

internal val periodicActionsThreadPool: ScheduledExecutorService by lazy {
    val tf = ThreadFactoryBuilder()
        .setNameFormat("scheduled-events-pool-%d")
        .build()
    Executors.newScheduledThreadPool(2, tf)
}

internal val cache = Cache(
    File("store"),
    50L * 1024L * 1024L // 50 MiB
)

internal val apiClient = DefaultApi(
    ApiClient(
        OkHttpClient.Builder()
            .readTimeout(Duration.ofSeconds(60))
            .connectTimeout(Duration.ofSeconds(60))
            .writeTimeout(Duration.ofSeconds(60))
            .cache(cache)
            .build()
    ).apply {
        basePath = "https://v2-api.lol-esports.mckernant1.com"
    }
)

package com.mckernant1.lol.blitzcrank.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.reactive.asFlow
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.utils.data.DataObject
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest
import java.io.File

fun String.file(): File = File(this)

internal fun commandDataFromJson(json: String): CommandData = CommandData.fromData(DataObject.fromJson(json))

@OptIn(ExperimentalCoroutinesApi::class)
fun <T : Any> DynamoDbAsyncTable<T>.scanAsFlow(
    request: ScanEnhancedRequest = ScanEnhancedRequest.builder().build()
): Flow<T> =
    this.scan(request)
        .asFlow()
        .flatMapConcat { page ->
            page.items().asFlow()
        }

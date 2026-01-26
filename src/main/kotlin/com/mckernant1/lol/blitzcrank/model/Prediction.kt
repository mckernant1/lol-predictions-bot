package com.mckernant1.lol.blitzcrank.model

import com.mckernant1.lol.blitzcrank.utils.ddbClient
import com.mckernant1.lol.blitzcrank.utils.scanAsFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.asFlow
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import kotlin.streams.toList

@DynamoDbImmutable(builder = Prediction.Builder::class)
class Prediction(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId")
    @get:DynamoDbSecondarySortKey(indexNames = [MATCH_ID_INDEX_NAME])
    val userId: String,
    @get:DynamoDbAttribute("matchId")
    @get:DynamoDbSortKey
    @get:DynamoDbSecondaryPartitionKey(indexNames = [MATCH_ID_INDEX_NAME])
    val matchId: String,
    val prediction: String,
) {
    override fun toString(): String = "[userId=$userId, matchId=$matchId, prediction=$prediction]"

    companion object {
        const val MATCH_ID_INDEX_NAME = "games-by-match-id-index"
        private val TABLE_NAME by lazy {
            System.getenv("PREDICTIONS_TABLE_NAME")
                ?: error("Environment variable 'PREDICTIONS_TABLE_NAME' is not defined")
        }
        private val table: DynamoDbAsyncTable<Prediction> by lazy {
            ddbClient.table(TABLE_NAME, TableSchema.fromImmutableClass(Prediction::class.java))
        }

        suspend fun getItem(userId: String, matchId: String): Prediction? =
            table.getItem(Key.builder().partitionValue(userId).sortValue(matchId).build())
                .await()

        suspend fun putItem(prediction: Prediction) {
            table.putItem(prediction).await()
        }

        fun scan(): Flow<Prediction> = table.scanAsFlow()

        suspend fun getAllPredictionsForMatch(matchId: String): List<Prediction> =
            table.index(MATCH_ID_INDEX_NAME).query { it ->
                it.queryConditional(
                    QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(matchId).build()
                    )
                )
            }.asFlow().toList().flatMap { it.items() }

        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var matchId: String? = null
        fun matchId(value: String) = apply { matchId = value }

        private var discordId: String? = null
        fun userId(value: String) = apply { discordId = value }

        private var prediction: String? = null
        fun prediction(value: String) = apply { prediction = value }

        fun build() = Prediction(
            matchId = matchId!!,
            userId = discordId!!,
            prediction = prediction!!
        )
    }
}

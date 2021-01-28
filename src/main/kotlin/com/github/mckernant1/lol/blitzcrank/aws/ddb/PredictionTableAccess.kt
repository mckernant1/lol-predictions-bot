package com.github.mckernant1.lol.blitzcrank.aws.ddb

import com.github.mckernant1.lol.blitzcrank.model.Prediction
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import java.util.stream.Collectors

class PredictionTableAccess {

    companion object {
        private val TABLE_NAME = System.getenv("PREDICTIONS_TABLE_NAME")
            ?: error("Environment variable 'PREDICTIONS_TABLE_NAME' is not defined")
        private val table: DynamoDbTable<Prediction> =
            ddbClient.table(TABLE_NAME, TableSchema.fromImmutableClass(Prediction::class.java))
    }


    fun getUsersPredictions(userId: String): List<Prediction> {
        val conditional = QueryConditional.keyEqualTo(
            Key.builder().partitionValue(userId).build()
        )
        val query = QueryEnhancedRequest.builder()
            .queryConditional(conditional)
            .build()
        return table.query(query).items().stream().collect(Collectors.toList())
    }

    fun putItem(prediction: Prediction) = table.putItem(prediction)
}

package com.github.mckernant1.lol.blitzcrank.aws.ddb

import com.github.mckernant1.lol.blitzcrank.model.Prediction
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

class PredictionTableAccess {

    companion object {
        private val TABLE_NAME = System.getenv("PREDICTIONS_TABLE_NAME")
            ?: error("Environment variable 'PREDICTIONS_TABLE_NAME' is now defined")
        private val table: DynamoDbTable<Prediction> =
            ddbClient.table(TABLE_NAME, TableSchema.fromImmutableClass(Prediction::class.java))
    }

    fun getItem(userId: String, matchId: String): Prediction? =
        table.getItem(Key.builder().partitionValue(userId).sortValue(matchId).build())

    fun addItem(prediction: Prediction) = table.putItem(prediction)
}

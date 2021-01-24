package com.github.mckernant1.lol.blitzcrank.aws.ddb

import com.github.mckernant1.lol.blitzcrank.model.Prediction
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Expression
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.util.stream.Collectors

class PredictionTableAccess {

    companion object {
        private val table: DynamoDbTable<Prediction> =
            ddbClient.table("discord-predictions", TableSchema.fromClass(Prediction::class.java))
    }


    fun getUsersPredictions(userId: String): List<Prediction> {
        val expression = Expression.builder()
            .expression("userId = $userId")
            .build()
        return table.query { r -> r.filterExpression(expression) }.items().stream().collect(Collectors.toList())
            ?: emptyList()

    }

    fun deletePrediction(userId: String, matchId: String) {
        val expression = Expression.builder()
            .expression("userId = $userId")
            .expression("matchId = $matchId")
            .build()
        val itemsToDelete =
            table.query { r -> r.filterExpression(expression) }.items().stream().collect(Collectors.toList())
                ?: emptyList()

        itemsToDelete.forEach {
            table.deleteItem(it)
        }
    }

    fun addItem(prediction: Prediction) = table.putItem(prediction)


}

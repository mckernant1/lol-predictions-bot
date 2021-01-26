package com.github.mckernant1.lol.blitzcrank.aws.ddb.predictions

import com.github.mckernant1.lol.blitzcrank.aws.ddb.ddbClient
import com.github.mckernant1.lol.blitzcrank.model.Prediction
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import java.util.stream.Collectors

class PredictionTableAccess : PredictionsAccess {

    companion object {
        private val table: DynamoDbTable<Prediction> =
            ddbClient.table("discord-predictions", TableSchema.fromImmutableClass(Prediction::class.java))
    }


    override fun getUsersPredictions(userId: String): List<Prediction> {
        return table.scan().items().stream()
            .filter { it.userId == userId }
            .collect(Collectors.toList())
    }

    override fun deletePrediction(userId: String, matchId: String) {
        val itemsToDelete = table.scan().items().stream()
            .filter { it.userId == userId && it.matchId == matchId}
            .collect(Collectors.toList())
        itemsToDelete.forEach {
            table.deleteItem(it)
        }
    }

    override fun addItem(prediction: Prediction) = table.putItem(prediction)


}

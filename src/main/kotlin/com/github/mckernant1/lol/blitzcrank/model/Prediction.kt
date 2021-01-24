package com.github.mckernant1.lol.blitzcrank.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbImmutable(builder = Prediction.Builder::class)
class Prediction(
    val matchId: String,
    val userId: String,
    val prediction: String,
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("UUID")
    val uuid: String = java.util.UUID.randomUUID().toString()
) {
    companion object {
        @JvmStatic
        fun builder() = Builder()
    }

    class Builder {
        private var uuid: String? = null
        fun uuid(value: String) = apply { uuid = value }

        private var matchId: String? = null
        fun matchId(value: String) = apply { matchId = value }

        private var userId: String? = null
        fun userId(value: String) = apply { userId = value }

        private var prediction: String? = null
        fun prediction(value: String) = apply { prediction = value }

        fun build() = Prediction(
            matchId!!,
            userId!!,
            prediction!!,
            uuid!!
        )
    }
}

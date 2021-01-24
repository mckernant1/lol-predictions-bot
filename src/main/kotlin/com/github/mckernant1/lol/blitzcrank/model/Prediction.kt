package com.github.mckernant1.lol.blitzcrank.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
data class Prediction (
    val matchId: String,
    val userId: String,
    val prediction: String,
    @get:DynamoDbPartitionKey val UUID: String = java.util.UUID.randomUUID().toString()
)

package com.github.mckernant1.lol.blitzcrank.aws.ddb

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.services.dynamodb.DynamoDbClient


private val dc = DynamoDbClient.builder().build()
internal val ddbClient: DynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
    .dynamoDbClient(dc)
    .build()


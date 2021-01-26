package com.github.mckernant1.lol.blitzcrank.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
class UserSettings(
    var timezone: String? = null,
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId") var discordId: String? = null,
)

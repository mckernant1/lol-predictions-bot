package com.github.mckernant1.lol.blitzcrank.model

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
class UserSettings(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId") var discordId: String? = null,
    var timezone: String? = null
) {
    override fun toString(): String = "[discordId=$discordId, timezone=$timezone]"
}

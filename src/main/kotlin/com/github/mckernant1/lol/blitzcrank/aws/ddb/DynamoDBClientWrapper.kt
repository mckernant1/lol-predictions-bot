package com.github.mckernant1.lol.blitzcrank.aws.ddb

import software.amazon.awssdk.services.dynamodb.DynamoDbClient

class DynamoDBClientWrapper {
    private val ddbClient = DynamoDbClient.builder()
        .build()

    fun getItem() {
        ddbClient.query {
            it.tableName("")
            it.select("")
        }
    }

}

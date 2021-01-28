package com.github.mckernant1.lol.blitzcrank.aws.ddb

import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

class UserSettingsTableAccess {

    companion object {
        private val TABLE_NAME = System.getenv("USER_SETTINGS_TABLE_NAME")
            ?: error("Environment variable 'USER_SETTINGS_TABLE_NAME' is not defined")
        private val table: DynamoDbTable<UserSettings> =
            ddbClient.table(TABLE_NAME, TableSchema.fromClass(UserSettings::class.java))
    }

    fun getSettingsForUser(discordId: String): UserSettings =
        table.getItem(Key.builder().partitionValue(discordId).build())
            ?: UserSettings(discordId = discordId)

    fun putSettings(settings: UserSettings) = table.putItem(settings)

}

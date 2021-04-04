package com.github.mckernant1.lol.blitzcrank.model

import com.github.mckernant1.lol.blitzcrank.utils.ddbClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey

@DynamoDbBean
class UserSettings(
    @get:DynamoDbPartitionKey
    @get:DynamoDbAttribute("discordId") var discordId: String? = null,
    var timezone: String = "America/Los_Angeles",
    var pasta: String = "You have no pasta. Set one with `!setPasta My Pasta!`"
) {

    companion object {
        private val TABLE_NAME by lazy {
            System.getenv("USER_SETTINGS_TABLE_NAME")
                ?: error("Environment variable 'USER_SETTINGS_TABLE_NAME' is not defined")
        }
        private val table: DynamoDbTable<UserSettings> by lazy {
            ddbClient.table(TABLE_NAME, TableSchema.fromClass(UserSettings::class.java))
        }

        fun getSettingsForUser(discordId: String): UserSettings =
            table.getItem(Key.builder().partitionValue(discordId).build())
                ?: UserSettings(discordId = discordId)

        fun putSettings(settings: UserSettings) = table.putItem(settings)
    }

    override fun toString(): String = "[discordId=$discordId, timezone=$timezone, pasta=$pasta]"
}

package com.github.mckernant1.lol.blitzcrank.aws.ddb.user

import com.github.mckernant1.lol.blitzcrank.aws.ddb.ddbClient
import com.github.mckernant1.lol.blitzcrank.model.UserSettings
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.TableSchema

class UserSettingsTableAccess : UserSettingsAccess {

    companion object {
        private val table: DynamoDbTable<UserSettings> =
            ddbClient.table("user-settings", TableSchema.fromClass(UserSettings::class.java))
    }

    override fun getSettingsForUser(discordId: String): UserSettings =
        table.getItem(Key.builder().partitionValue(discordId).build())
            ?: UserSettings(discordId = discordId)

    override fun putSettings(settings: UserSettings) = table.putItem(settings)

}

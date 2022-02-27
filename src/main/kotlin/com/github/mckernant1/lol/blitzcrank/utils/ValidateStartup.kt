package com.github.mckernant1.lol.blitzcrank.utils

fun validateEnvironment() {
    val unSet = listOf(
        "BOT_TOKEN",
        "ESPORTS_API_KEY",
        "PREDICTIONS_TABLE_NAME",
        "USER_SETTINGS_TABLE_NAME"
    ).map {
        it to (System.getenv(it) ?: null)
    }.filter { (_, value) ->
        value == null
    }.map { (key, _) -> key }

    if (unSet.isNotEmpty()) {
        error("Environment variables '$unSet' are not defined")
    }

}

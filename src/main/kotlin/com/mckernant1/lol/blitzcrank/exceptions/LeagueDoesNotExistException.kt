package com.mckernant1.lol.blitzcrank.exceptions

class LeagueDoesNotExistException(
    message: String? = null,
    cause: Throwable? = null,
) : InvalidCommandException(message, cause) {
}

package com.github.mckernant1.lol.blitzcrank.exceptions

class TeamDoesNotExistException(
    message: String? = null,
    cause: Throwable? = null,
) : InvalidCommandException(message, cause) {
}

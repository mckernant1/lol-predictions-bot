package com.mckernant1.lol.blitzcrank.exceptions

open class InvalidCommandException(
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)

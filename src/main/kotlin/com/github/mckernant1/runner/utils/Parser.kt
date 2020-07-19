package com.github.mckernant1.runner.utils

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

fun getRegionAndNumber(message: Message): Pair<String, Int> {
    val words = getWordsFromMessage(message)
    val region = words[1]
    val num = words.getOrNull(2)?.toInt() ?: 3

    return Pair(region, num)
}

fun getRegion(message: Message): String {
    val words = getWordsFromMessage(message)
    return words[1]
}



fun validateAndParseRegionAndNumberForResultsAndSchedule(event: MessageReceivedEvent): Pair<String, Int>? {
    val message = event.message
    val words = getWordsFromMessage(message)

    if (!validateWordCount(words) { it in 2..3 }) {
        reactUserError(message)
        return null
    }

    val (region, numToGet) = getRegionAndNumber(message)

    if (!(validateRegion(region) && validateNumberOfMatches(
            numToGet
        ))) {
        reactUserError(message)
        return null
    }
    return Pair(region, numToGet)
}

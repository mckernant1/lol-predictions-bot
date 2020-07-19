package com.github.mckernant1.runner.utils

fun validateWordCount(words: List<String>, lenFunction: (Int) -> Boolean): Boolean = lenFunction(words.size)
    .also { println("validateWordCount running with words: $words and result: $it") }

fun validateRegion(region: String): Boolean = (getLeagues().find { it.name.equals(region, ignoreCase = true) } != null)
    .also { println("validateRegion with region: $region and result: $it") }

fun validateNumberOfMatches(num: Int): Boolean = (num in 1..19)
    .also { println("validateNumberOfMatches with number $num and result: $it") }

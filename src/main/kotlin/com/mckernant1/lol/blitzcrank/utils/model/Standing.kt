package com.mckernant1.lol.blitzcrank.utils.model

data class Standing(
    val teamCode: String,
    val wins: MutableList<String> = mutableListOf(),
    val losses: MutableList<String> = mutableListOf()
)

package com.github.mckernant1.runner

//fun predictCmd(
//    words: List<String>,
//    event: MessageReceivedEvent
//) {
//    val (region, numToGet) = validateToRegionAndNumberOfGames(words, event) ?: return
//    val matches = getSchedule(region, numToGet)
//    val replies = formatReplies(matches)
//    runBlocking {
//        val messagesToWatch = replies.map { (msg, date) ->
//            val m = event.channel.sendMessage(msg).complete()
//            m.addReaction("\uD83D\uDD35").complete()
//            m.addReaction("\uD83D\uDD34").complete()
//            return@map m
//        }
//    }
//}


//private fun formatReplies(matches: List<Match>): List<Pair<String, Date>> {
//    return matches.map {
//        "${it.date}: \uD83D\uDD35 ${it.team1} vs ${it.team2} \uD83D\uDD34" to it.date
//    }
//}
//

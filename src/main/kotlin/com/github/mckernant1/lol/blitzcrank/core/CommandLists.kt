package com.github.mckernant1.lol.blitzcrank.core

import com.github.mckernant1.lol.blitzcrank.commands.CommandMetadata
import com.github.mckernant1.lol.blitzcrank.commands.info.InfoCommand
import com.github.mckernant1.lol.blitzcrank.commands.lol.*
import com.github.mckernant1.lol.blitzcrank.commands.misc.NotifyMeCommand
import com.github.mckernant1.lol.blitzcrank.commands.misc.SetTimezoneCommand
import com.github.mckernant1.lol.blitzcrank.commands.pasta.PastaCommand
import com.github.mckernant1.lol.blitzcrank.commands.pasta.SetPastaCommand
import com.github.mckernant1.lol.blitzcrank.commands.reminder.AddReminderCommand
import com.github.mckernant1.lol.blitzcrank.commands.reminder.ListRemindersCommand
import com.github.mckernant1.lol.blitzcrank.commands.reminder.RemoveReminderCommand

internal val commandsByType: Map<String, List<CommandMetadata>> by lazy {
    mapOf(
        "esports" to listOf(
            OngoingTournamentsCommand,
            ResultsCommand,
            RosterCommand,
            ScheduleCommand,
            StandingsCommand
        ),
        "predict" to listOf(
            ReportCommand.Predictions,
            ReportCommand.Report,
            PredictCommand,
            StatsCommand
        ),
        "settings" to listOf(
            NotifyMeCommand,
            SetTimezoneCommand
        ),
        "pasta" to listOf(
            PastaCommand,
            SetPastaCommand
        ),
        "reminder" to listOf(
            AddReminderCommand,
            ListRemindersCommand,
            RemoveReminderCommand
        )
    )
}

internal val commandList: Set<CommandMetadata> by lazy {
    val set = commandsByType.values.flatten().toMutableSet()
    set.add(InfoCommand)
    set
}

internal val commandsByName: Map<String, CommandMetadata> by lazy {
    commandList.associateBy { it.commandString }
}


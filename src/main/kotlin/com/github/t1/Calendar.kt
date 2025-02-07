package com.github.t1

import com.github.t1.bulmajava.basic.AbstractElement
import com.github.t1.bulmajava.basic.Basic.h2
import com.github.t1.bulmajava.basic.Color.INFO
import com.github.t1.bulmajava.basic.Color.LINK
import com.github.t1.bulmajava.basic.Color.PRIMARY
import com.github.t1.bulmajava.basic.Renderable
import com.github.t1.bulmajava.basic.Style.BLACK
import com.github.t1.bulmajava.basic.Style.DARK
import com.github.t1.bulmajava.basic.Style.GHOST
import com.github.t1.bulmajava.elements.Tag
import com.github.t1.bulmajava.elements.Tag.tag
import com.github.t1.bulmajava.grid.Grid.cell
import com.github.t1.bulmajava.grid.Grid.fixedGrid
import com.github.t1.bulmajava.layout.Container.container
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle.FULL
import java.time.format.TextStyle.SHORT_STANDALONE
import java.util.Locale
import java.util.stream.Stream

class Calendar : AbstractElement<Calendar> {
    private val locale = Locale.getDefault()!!
    private val project: Project
    private val firstDay: LocalDate
    private val now: LocalDate = LocalDate.now()
    private val lastDay: LocalDate
    private val lastDayRemaining: WorkLog

    constructor(project: Project, remaining: WorkLog) : super("div") {
        this.project = project
        this.firstDay = project.start
        project.lastDay(remaining).let { (lastDay, lastDayRemaining) ->
            this.lastDay = lastDay
            this.lastDayRemaining = lastDayRemaining
        }

        this containing calendarGrid()
    }

    private fun calendarGrid() = fixedGrid(3) containing
            (YearMonth.from(firstDay)..YearMonth.from(lastDay))
                .map { cell() containing monthView(it) }

    private fun monthView(yearMonth: YearMonth) =
        container() containing monthHeader(yearMonth) containing monthGrid(yearMonth)

    private fun monthHeader(yearMonth: YearMonth) =
        container() containing h2("${yearMonth.month.fullName()} / ${yearMonth.year}")

    private fun monthGrid(yearMonth: YearMonth) =
        fixedGrid(7) containing weekDays() containing daysGrid(yearMonth)

    private fun weekDays() =
        DayOfWeek.entries.stream().map<Renderable> { cell() containing tag(it.shortName()) }

    private fun daysGrid(yearMonth: YearMonth) = yearMonth.days().stream().map<Renderable> {
        val cell = cell()
        if (it.dayOfMonth == 1) cell.isColStart(it.dayOfWeek.value)
        cell containing dayTag(it)
    }

    private fun dayTag(date: LocalDate): Tag {
        val onVacation = project.team.values.filter { member -> member.isInVacationAt(date) }
        val allOnVacation = onVacation.size == project.team.size
        val inOpt = project.team.values.filter { member -> member.hasOpt(date) }
        val (color, title) = when {
            date in project.holidays -> BLACK to project.holidays[date]!!
            date.dayOfWeek in WEEKEND -> GHOST to "weekend"
            date < firstDay -> GHOST to "before start"
            date < now -> LINK to "done"
            allOnVacation -> DARK to "all on vacation"
            date == lastDay -> INFO to "last day: ${lastDayRemaining.fractionalHours.roundTo(2)}h"
            onVacation.isNotEmpty() -> INFO to "on vacation: " + onVacation.joinToString { it.name ?: "?" }
            inOpt.isNotEmpty() -> LINK to "OPT: " + inOpt.joinToString { it.name ?: "?" }
            date > lastDay -> GHOST to "after end"
            else -> PRIMARY to "all in"
        }
        return tag(date.dayOfMonth.toString())
            .`is`(color)
            .attr("title", title)
    }

    private fun DayOfWeek.shortName() = getDisplayName(SHORT_STANDALONE, locale)
    private fun Month.fullName() = getDisplayName(FULL, locale)
}

@Suppress("UNCHECKED_CAST")
infix fun <T : AbstractElement<*>> T.containing(element: Renderable): T = content(element) as T

@Suppress("UNCHECKED_CAST")
infix fun <T : AbstractElement<*>> T.containing(element: Stream<Renderable>): T = content(element) as T

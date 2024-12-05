package com.github.t1

import kotlinx.serialization.Serializable

@Serializable
data class WorkLog(val totalMinutes: Int) {
    constructor(
        weeks: Int,
        days: Int,
        hours: Int,
        minutes: Int
    ) : this(weeks * 5 * 8 * 60 + days * 8 * 60 + hours * 60 + minutes)

    @Suppress("unused")
    constructor(expression: String) : this(parse(expression).totalMinutes)

    val totalHours get() = totalMinutes / 60
    val totalDays get() = totalHours / 8
    val totalWeeks get() = totalDays / 5

    val weeksPart = totalWeeks
    val daysPart = totalDays % 5
    val hoursPart = totalHours % 8
    val minutesPart = totalMinutes % 60

    val fractionalWeeks get() = fractionalDays / 5
    val fractionalDays get() = fractionalHours / 8
    val fractionalHours get() = totalMinutes.toDouble() / 60

    val isZero get() = totalMinutes == 0

    override fun toString() = buildString {
        if (weeksPart != 0) append(weeksPart).append("w ")
        if (daysPart != 0) append(daysPart).append("d ")
        if (hoursPart != 0) append(hoursPart).append("h ")
        if (minutesPart != 0) append(minutesPart).append("m")
    }.trim()

    operator fun unaryMinus() = WorkLog(-totalMinutes)
    operator fun plus(subtrahend: WorkLog) =
        if (subtrahend.isZero) this else WorkLog(totalMinutes + subtrahend.totalMinutes)

    operator fun minus(subtrahend: WorkLog) =
        if (subtrahend.isZero) this else WorkLog(totalMinutes - subtrahend.totalMinutes)

    companion object {
        fun parse(expression: String): WorkLog {
            val groups = PATTERN.find(expression)?.groups
            fun group(name: String): Int = (groups?.get(name)?.value?.toInt() ?: 0)

            val weeks = group("weeks")
            val days = group("days")
            val hours = group("hours")
            val minutes = group("minutes")
            return WorkLog(weeks, days, hours, minutes)
        }

        private val PATTERN =
            """((?<weeks>\d+)w)? *((?<days>\d+)d)? *((?<hours>\d+)h)? *((?<minutes>\d+)m)? *""".toRegex()
    }
}

val Int.weeks get() = WorkLog(this, 0, 0, 0)
val Int.days get() = WorkLog(0, this, 0, 0)
val Int.hours get() = WorkLog(0, 0, this, 0)
val Int.minutes get() = WorkLog(0, 0, 0, this)

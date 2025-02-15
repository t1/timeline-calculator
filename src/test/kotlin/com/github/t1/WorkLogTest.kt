package com.github.t1

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test

class WorkLogTest {
    @Test
    fun shouldParseFullExpression() {
        val workLog = WorkLog("1w 2d 3h 4m")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(3)
        then(workLog.minutesPart).isEqualTo(4)
        then(workLog.totalWeeks).isEqualTo(1)
        then(workLog.totalDays).isEqualTo(7)
        then(workLog.totalHours).isEqualTo(59)
        then(workLog.totalMinutes).isEqualTo(3544)
        then(workLog.fractionalWeeks.roundTo(3)).isEqualTo(1.477)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(7.383)
        then(workLog.fractionalHours.roundTo(3)).isEqualTo(59.067)
        then(workLog.toString()).isEqualTo("1w 2d 3h 4m")
    }

    @Test
    fun shouldParseNoWeeksExpression() {
        val workLog = WorkLog("2d 3h 4m")

        then(workLog.weeksPart).isEqualTo(0)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(3)
        then(workLog.minutesPart).isEqualTo(4)
        then(workLog.totalWeeks).isEqualTo(0)
        then(workLog.totalDays).isEqualTo(2)
        then(workLog.totalHours).isEqualTo(19)
        then(workLog.totalMinutes).isEqualTo(1144)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(2.383)
        then(workLog.toString()).isEqualTo("2d 3h 4m")
    }

    @Test
    fun shouldParseNoDaysExpression() {
        val workLog = WorkLog("1w 3h 4m")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(0)
        then(workLog.hoursPart).isEqualTo(3)
        then(workLog.minutesPart).isEqualTo(4)
        then(workLog.totalWeeks).isEqualTo(1)
        then(workLog.totalDays).isEqualTo(5)
        then(workLog.totalHours).isEqualTo(43)
        then(workLog.totalMinutes).isEqualTo(2584)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(5.383)
        then(workLog.toString()).isEqualTo("1w 3h 4m")
    }

    @Test
    fun shouldParseNoHoursExpression() {
        val workLog = WorkLog("1w 2d 4m")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(0)
        then(workLog.minutesPart).isEqualTo(4)
        then(workLog.totalWeeks).isEqualTo(1)
        then(workLog.totalDays).isEqualTo(7)
        then(workLog.totalHours).isEqualTo(56)
        then(workLog.totalMinutes).isEqualTo(3364)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(7.008)
        then(workLog.toString()).isEqualTo("1w 2d 4m")
    }

    @Test
    fun shouldParseNoMinutesExpression() {
        val workLog = WorkLog("1w 2d 3h")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(3)
        then(workLog.minutesPart).isEqualTo(0)
        then(workLog.totalWeeks).isEqualTo(1)
        then(workLog.totalDays).isEqualTo(7)
        then(workLog.totalHours).isEqualTo(59)
        then(workLog.totalMinutes).isEqualTo(3540)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(7.375)
        then(workLog.toString()).isEqualTo("1w 2d 3h")
    }

    @Test
    fun shouldParseWeeksAndHoursExpression() {
        val workLog = WorkLog("1w 2d")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(0)
        then(workLog.minutesPart).isEqualTo(0)
        then(workLog.totalWeeks).isEqualTo(1)
        then(workLog.totalDays).isEqualTo(7)
        then(workLog.totalHours).isEqualTo(56)
        then(workLog.totalMinutes).isEqualTo(3360)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(7.0)
        then(workLog.toString()).isEqualTo("1w 2d")
    }

    @Test
    fun shouldParseWeeksExpression() {
        val workLog = WorkLog("1w")

        then(workLog.weeksPart).isEqualTo(1)
        then(workLog.daysPart).isEqualTo(0)
        then(workLog.hoursPart).isEqualTo(0)
        then(workLog.minutesPart).isEqualTo(0)
        then(workLog.totalDays).isEqualTo(5)
        then(workLog.totalHours).isEqualTo(40)
        then(workLog.totalMinutes).isEqualTo(2400)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(5.0)
        then(workLog.toString()).isEqualTo("1w")
    }

    @Test
    fun shouldParseDaysExpression() {
        val workLog = WorkLog("2d")

        then(workLog.weeksPart).isEqualTo(0)
        then(workLog.daysPart).isEqualTo(2)
        then(workLog.hoursPart).isEqualTo(0)
        then(workLog.minutesPart).isEqualTo(0)
        then(workLog.totalDays).isEqualTo(2)
        then(workLog.totalHours).isEqualTo(16)
        then(workLog.totalMinutes).isEqualTo(960)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(2.0)
        then(workLog.toString()).isEqualTo("2d")
    }

    @Test
    fun shouldParseOverflowingExpression() {
        val workLog = WorkLog("1w 23d 45h 67m")

        then(workLog.totalMinutes).isEqualTo(16207)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(33.765)
        then(workLog.toString()).isEqualTo("6w 3d 6h 7m")
    }

    @Test
    fun shouldParseFloatingPointDays() {
        val workLog = WorkLog("1.5d")

        then(workLog.totalWeeks).isEqualTo(0)
        then(workLog.totalDays).isEqualTo(1)
        then(workLog.totalHours).isEqualTo(12)
        then(workLog.totalMinutes).isEqualTo(720)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(1.500)
        then(workLog.toString()).isEqualTo("1d 4h")
    }

    @Test
    fun shouldParseFloatingPointHours() {
        val workLog = WorkLog("1d 1.75h")

        then(workLog.totalWeeks).isEqualTo(0)
        then(workLog.totalDays).isEqualTo(1)
        then(workLog.totalHours).isEqualTo(9)
        then(workLog.totalMinutes).isEqualTo(585)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(1.219)
        then(workLog.toString()).isEqualTo("1d 1h 45m")
    }

    @Test
    fun shouldParseFloatingPointHours2() {
        val workLog = WorkLog("51d 6.75h")

        then(workLog.totalWeeks).isEqualTo(10)
        then(workLog.totalDays).isEqualTo(51)
        then(workLog.totalHours).isEqualTo(414)
        then(workLog.totalMinutes).isEqualTo(24885)
        then(workLog.fractionalDays.roundTo(3)).isEqualTo(51.844)
        then(workLog.toString()).isEqualTo("10w 1d 6h 45m")
    }

    @Test
    fun shouldCreateFromInt() {
        then(1.weeks.toString()).isEqualTo("1w")
        then(2.days.toString()).isEqualTo("2d")
        then(3.hours.toString()).isEqualTo("3h")
        then(4.minutes.toString()).isEqualTo("4m")
    }

    @Test
    fun shouldAdd() {
        then(1.days + 2.days).isEqualTo(3.days)
        then(2.weeks + 6.days).isEqualTo(16.days)
        then(1.days + 3.hours).isEqualTo(11.hours)
        then(1.hours + 15.minutes).isEqualTo(75.minutes)
    }

    @Test
    fun shouldSubtract() {
        then(1.weeks - 2.days).isEqualTo(3.days)
        then(2.weeks - 6.days).isEqualTo(4.days)
        then(1.days - 3.hours).isEqualTo(5.hours)
        then(1.hours - 15.minutes).isEqualTo(45.minutes)
        then(12.weeks - 34.days).isEqualTo(26.days)
    }

    @Test
    fun shouldWorkWithNegativeWorkLogs() {
        then(-(1.weeks) + 2.days).isEqualTo((-3).days)
        then(-(1.weeks) + (-2).days).isEqualTo((-7).days)
        then((-1).weeks + 2.days).isEqualTo((-3).days)
        then(1.weeks - 2.weeks).isEqualTo((-1).weeks)
        then((-1).weeks + 2.days).hasToString("-3d")
        then((-1).weeks - 2.days).hasToString("-1w -2d")
        then((-1).weeks - 2.days - 3.hours - 4.minutes).hasToString("-1w -2d -3h -4m")
        then((-3).hours - 4.minutes).hasToString("-3h -4m")
        then((-4).minutes).hasToString("-4m")
    }

    // before: 12w 4d 5h 15m
    // after : 12w 3d 5h 15m
}

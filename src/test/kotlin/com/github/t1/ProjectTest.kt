package com.github.t1

import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ProjectTest {
    val day0: LocalDate = LocalDate.of(2025, 4, 1) // a Tuesday
    val project = Project(today = day0)

    @Test
    fun shouldCalculateLastDayFrom0hours() {
        then(project.lastDay(0.hours)).isEqualTo(day0 to 0.minutes)
    }

    @Test
    fun shouldCalculateLastDayFrom4hours() {
        then(project.lastDay(4.hours)).isEqualTo(day0 to 4.hours)
    }

    @Test
    fun shouldCalculateLastDayFrom8hours() {
        then(project.lastDay(8.hours)).isEqualTo(day0 to 8.hours)
    }

    @Test
    fun shouldCalculateLastDayFrom10hours() {
        then(project.lastDay(10.hours)).isEqualTo(day0 + 1 to 2.hours)
    }

    @Test
    fun shouldCalculateLastDayFrom16hours() {
        then(project.lastDay(16.hours)).isEqualTo(day0 + 1 to 8.hours)
    }

    @Test
    fun shouldCalculateLastDayFrom4days() {
        then(project.lastDay(4.days)).isEqualTo(day0 + 3 to 8.hours)
    }

    @Test
    fun shouldCalculateLastDayAfterWeekendFrom4daysAnd1hour() {
        then(project.lastDay(4.days + 1.hours)).isEqualTo(day0 + 6 to 1.hours)
    }

    @Test
    fun shouldCalculateLastDayAfterHolidayFrom2days() {
        project.holidays[day0] = "fools day"
        then(project.lastDay(2.days)).isEqualTo(day0 + 2 to 8.hours)
    }

    @Test
    fun shouldCalculateLastDayAfterHolidayFrom2hours() {
        project.holidays[day0] = "fools day"
        then(project.lastDay(2.hours)).isEqualTo(day0 + 1 to 2.hours)
    }

    @Test
    fun shouldCalculateLastDayAfterTwoDaysOfVacationFrom1weekAnd10minutes() {
        project.team["A"]?.vacations?.add(Vacation(day0 + 1, day0 + 2))
        then(project.lastDay(1.weeks + 10.minutes)).isEqualTo(day0 + 9 to 10.minutes)
    }

    @Test
    fun shouldCalculateLastDayFor2TeamMembersFrom2weeks() {
        project.team["B"] = TeamMember("Bob")
        then(project.lastDay(2.weeks)).isEqualTo(day0 + 6 to 16.hours)
    }

    @Test
    fun shouldCalculateLastDayFor2TeamMembersFrom72hours() {
        project.team["B"] = TeamMember("Bob")
        then(project.lastDay(72.hours)).isEqualTo(day0 + 6 to 8.hours)
    }
}

package com.github.t1

import kotlinx.serialization.encodeToString
import org.assertj.core.api.BDDAssertions.catchThrowableOfType
import org.assertj.core.api.BDDAssertions.contentOf
import org.assertj.core.api.BDDAssertions.then
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Path
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
        then(project.lastDay(4.days)).isEqualTo(day0 + 6 to 8.hours)
    }

    @Test
    fun shouldCalculateLastDayAfterWeekendFrom4daysAnd1hour() {
        then(project.lastDay(4.days + 1.hours)).isEqualTo(day0 + 7 to 1.hours)
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
        then(project.lastDay(1.weeks + 10.minutes)).isEqualTo(day0 + 13 to 10.minutes)
    }

    @Test
    fun shouldCalculateLastDayFor2TeamMembersFrom2weeks() {
        project.team["B"] = TeamMember("Bob")
        then(project.lastDay(2.weeks)).isEqualTo(day0 + 7 to 16.hours)
    }

    @Test
    fun shouldCalculateLastDayFor2TeamMembersFrom72hours() {
        project.team["B"] = TeamMember("Bob")
        then(project.lastDay(72.hours)).isEqualTo(day0 + 7 to 8.hours)
    }

    @Test
    fun shouldFailToLoadNonExitingYamlFile() {
        val exception = catchThrowableOfType(IllegalStateException::class.java, {
            ProjectLoader().loadProject(Path.of("non-existing.yaml"))
        })

        then(exception).hasMessageContaining("non-existing.yaml")
    }

    @Test
    fun shouldLoadProjectFromYamlFile() {
        val projectYamlPath = Path.of("src/test/resources/test-project.yaml")
        val project = ProjectLoader().loadProject(projectYamlPath)

        then(project.name).isEqualTo("Example Project")
        then(project.start).isEqualTo(day0)
        then(project.holidays).containsEntry(day0, "Fool's Day")
        then(project.team.size).isEqualTo(2)
        val alice = project.team["Alice"]!!
        then(alice.opt).isTrue
        then(alice.vacations).containsExactly(
            Vacation(LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 16))
        )
        then(alice.hasOpt(day0)).isFalse
        then(alice.hasOpt(day0 + 3)).isTrue
        val bob = project.team["Bob"]!!
        then(bob.opt).isFalse
        then(bob.hasOpt(day0)).isFalse
        then(bob.hasOpt(day0 + 3)).isFalse

        then(YAML.encodeToString(project)).isEqualTo(contentOf(projectYamlPath.toFile()).trim())
    }

    @Test
    fun shouldLoadDefaultYamlFile() {
        val project = ProjectLoader().loadProject()

        val realProjectYaml = Path.of("project.yaml")
        if (Files.exists(realProjectYaml)) {
            val realProjectName = Files.lines(realProjectYaml)
                .filter { it.startsWith("name:") }
                .findFirst()
                .map { it.substringAfter(":").trim() }
                .orElseThrow { IllegalStateException("no name found in $realProjectYaml") }
            then(project.name).isEqualTo(realProjectName)
        } else {
            then(project.name).isEqualTo("Unnamed Project")
        }
    }
}

package com.github.t1

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.decodeFromStream
import jakarta.enterprise.context.RequestScoped
import jakarta.enterprise.inject.Produces
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.LocalDate

val WEEKEND = setOf(SATURDAY, SUNDAY)

val YAML = Yaml(configuration = YamlConfiguration(strictMode = false))

private val PROJECT_YAML: Path = Paths.get("project.yaml")

class ProjectLoader {
    @Produces
    @RequestScoped
    fun loadProject(): Project =
        if (Files.exists(PROJECT_YAML))
            YAML.decodeFromStream<Project>(Files.newInputStream(PROJECT_YAML))
                .normalize()
        else Project()
}

@Serializable
data class Project(
    var name: String = "Unnamed Project",

    @Serializable(with = LocalDateSerializer::class)
    var today: LocalDate = LocalDate.now(),

    @Serializable(with = LocalDateSerializer::class)
    var start: LocalDate = today,

    var team: MutableMap<String, TeamMember> = mutableMapOf("A" to TeamMember("Alice")), // => default team size = 1

    var holidays: MutableMap<@Serializable(with = LocalDateSerializer::class) LocalDate, String> = mutableMapOf(),
) {
    fun normalize(): Project {
        team.forEach { key, member -> if (member.name == null) member.name = key }
        return this
    }

    /** Calculate the project day and the remaining hours on the last day, given the remaining duration */
    fun lastDay(duration: WorkLog): Pair<LocalDate, WorkLog> {
        var day = today
        var remaining = duration
        while (true) {
            val workingTeamMembers = if (day in holidays) 0 else team.values.count { member -> member.isWorkingAt(day) }
            if (remaining.fractionalDays <= workingTeamMembers) break
            remaining -= workingTeamMembers.days
            day += 1
        }
        return day to remaining
    }
}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", STRING)
    override fun serialize(encoder: Encoder, value: LocalDate) = encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): LocalDate = LocalDate.parse(decoder.decodeString())
}

@Serializable
data class TeamMember(
    var name: String? = null,
    var vacations: MutableList<Vacation> = mutableListOf()
) {
    fun isWorkingAt(date: LocalDate) = !hasFreeAt(date)
    fun hasFreeAt(date: LocalDate) = isInVacationAt(date) || date.dayOfWeek in WEEKEND
    fun isInVacationAt(date: LocalDate) = vacations.any { date in it }
}

@Serializable(with = VacationAsStringSerializer::class)
data class Vacation(
    var first: LocalDate,
    var last: LocalDate
) {
    operator fun contains(day: LocalDate) = first <= day && day <= last // faster than `day in first..last`
}

object VacationAsStringSerializer : KSerializer<Vacation> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Vacation", STRING)

    override fun serialize(encoder: Encoder, value: Vacation) {
        encoder.encodeString("${value.first}..${value.last}")
    }

    override fun deserialize(decoder: Decoder): Vacation {
        val string = decoder.decodeString()
        return Vacation(
            LocalDate.parse(string.substringBefore("..").trim()),
            LocalDate.parse(string.substringAfter("..").trim())
        )
    }
}

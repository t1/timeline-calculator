package com.github.t1

import com.github.t1.YamlMessageBodyWriter.Companion.APPLICATION_YAML
import com.github.t1.bulmajava.basic.Body.body
import com.github.t1.bulmajava.basic.Html
import com.github.t1.bulmajava.basic.Html.html
import com.github.t1.bulmajava.basic.Renderable
import com.github.t1.bulmajava.elements.Title.subtitle
import com.github.t1.bulmajava.elements.Title.title
import com.github.t1.bulmajava.form.Field.field
import com.github.t1.bulmajava.form.Form.form
import com.github.t1.bulmajava.form.Input.input
import com.github.t1.bulmajava.form.InputType.TEXT
import com.github.t1.bulmajava.layout.Container.container
import com.github.t1.bulmajava.layout.Section.section
import jakarta.ws.rs.FormParam
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType.APPLICATION_JSON
import jakarta.ws.rs.core.MediaType.TEXT_HTML
import jakarta.ws.rs.core.Response
import java.math.RoundingMode.HALF_UP
import java.net.URI
import java.time.LocalDate
import java.time.YearMonth
import java.util.stream.Stream

@Path("/")
class Application(val project: Project) {
    @GET
    @Produces(TEXT_HTML)
    fun index() = page(durationForm())

    private fun durationForm(): Renderable = form()
        .post("/")
        .content(
            field().label("Remaining Time Budget").control(
                input(TEXT).name("remaining").autofocus().required()
                    .placeholder("1w 2d 3h 4m")
            )
        )

    // TODO this doesn't work: it serializes the values of a default Project, not the one we return,
    //  and the @Produces annotation should be unnecessary!
    @GET
    @Produces(APPLICATION_JSON, APPLICATION_YAML)
    fun project() = project

    @POST
    fun post(@FormParam("remaining") expression: String): Response =
        Response.seeOther(URI.create("/" + expression.replace(" ", ""))).build()

    @GET
    @Path("/{remaining}")
    fun calendar(@PathParam("remaining") remaining: WorkLog): Html {
        val remainingText = "$remaining = ${remaining.fractionalDays.roundTo(1)} days"
        val daysPerMember = if (project.team.size <= 1) "" else " with a team of ${project.team.size} " +
                "(${(remaining.fractionalDays / project.team.size).roundTo(1)} days each)"
        return page(
            subtitle(4, remainingText + daysPerMember),
            Calendar(project, remaining)
        )
    }

    fun page(vararg content: Renderable): Html {
        val title = "Timeline Calculator" + (project.name.let { ": $it" })
        return html(title)
            .stylesheet("/webjars/fortawesome__fontawesome-free/css/all.css")
            .stylesheet("/webjars/bulma/css/bulma.css")
            .content(
                body().content(
                    container().content(
                        section()
                            .content(title(title))
                            .content(*content)
                    )
                )
            )!!
    }

    @GET
    @Path("/{remaining}/days")
    fun days(@PathParam("remaining") remaining: WorkLog) = remaining.totalDays

    @GET
    @Path("/{remaining}/hours")
    fun hours(@PathParam("remaining") remaining: WorkLog) = remaining.totalHours
}


fun Double.roundTo(digits: Int) = toBigDecimal().setScale(digits, HALF_UP).toDouble()

operator fun LocalDate.rangeTo(end: LocalDate): List<LocalDate> = this.datesUntil(end + 1).toList()
operator fun LocalDate.plus(days: Long): LocalDate = plusDays(days)

fun YearMonth.days(): List<LocalDate> = atDay(1)..atEndOfMonth()
operator fun YearMonth.plus(months: Long): YearMonth = plusMonths(months)
operator fun YearMonth.rangeTo(end: YearMonth): Stream<YearMonth> =
    Stream.iterate(this) { it + 1 }.takeWhile { it <= end }

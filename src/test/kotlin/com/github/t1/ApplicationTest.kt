package com.github.t1

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class ApplicationTest {
    @Test
    fun shouldCalculateDays() {
        given()
            .`when`().get("13w 1d 5h 45m/days")
            .then()
            .statusCode(200)
            .body(`is`("66"))
    }

    @Test
    fun shouldCalculateHours() {
        given()
            .`when`().get("13w 1d 5h 45m/hours")
            .then()
            .statusCode(200)
            .body(`is`("533"))
    }
}

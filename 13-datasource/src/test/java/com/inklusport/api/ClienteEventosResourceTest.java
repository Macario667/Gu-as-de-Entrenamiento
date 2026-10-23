package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;

@QuarkusTest
class ClienteEventosResourceTest {

    @Test
    void consumeEventosDeFormaSincrona() {
        given()
                .when().get("/api/cliente/eventos/sync")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)));
    }

    @Test
    void consumeEventosDeFormaAsincrona() {
        given()
                .when().get("/api/cliente/eventos/async")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)));
    }
}

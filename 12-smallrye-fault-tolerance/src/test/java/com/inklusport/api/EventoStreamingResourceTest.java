package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class EventoStreamingResourceTest {

    @Test
    void emiteLosEventosEnUnArrayJson() {
        given()
                .when().get("/api/stream/eventos")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("nombre", hasItem(notNullValue()));
    }

    @Test
    void emiteUnStatusYCabecerasPropios() {
        given()
                .when().get("/api/stream/eventos-personalizado")
                .then()
                .statusCode(207)
                .header("X-Stream", equalTo("true"));
    }

    @Test
    void emiteUnaSecuenciaDeNumeros() {
        given()
                .when().get("/api/stream/secuencia")
                .then()
                .statusCode(200)
                .body("$", hasSize(5))
                .body("[0]", equalTo(1));
    }

    @Test
    void elFlujoDeEventosDevuelveLosEventos() {
        given()
                .when().get("/api/reactivo/eventos/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("nombre", notNullValue());
    }

    @Test
    void elServerSentEventsEmiteEventos() {
        String cuerpo = given()
                .when().get("/api/sse/eventos")
                .then()
                .statusCode(200)
                .contentType(containsString("text/event-stream"))
                .extract().body().asString();

        assertTrue(cuerpo.contains("Evento 1"));
    }
}

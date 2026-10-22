package com.inklusport.logging;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class LoggingResourceTest {

    @Test
    void escribeEnElNivelSolicitado() {
        given()
                .queryParam("nivel", "DEBUG")
                .when().get("/api/logging/niveles")
                .then()
                .statusCode(200)
                .body("nivel", equalTo("DEBUG"))
                .body("mensaje", notNullValue());
    }

    @Test
    void elMdcGuardaElContextoDeLaPeticion() {
        given()
                .queryParam("usuario", "ana")
                .when().get("/api/logging/mdc")
                .then()
                .statusCode(200)
                .body("usuario", equalTo("ana"))
                .body("operacion", equalTo("consultaMDC"))
                .body("requestId", notNullValue());
    }

    @Test
    void cadaPeticionRecibeUnIdentificadorDeCorrelacion() {
        String id = given()
                .when().get("/api/logging/estructurado")
                .then()
                .statusCode(200)
                .extract().header("X-Request-Id");

        org.junit.jupiter.api.Assertions.assertNotNull(id);
        org.junit.jupiter.api.Assertions.assertTrue(
                id.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"),
                "El identificador de peticion deberia ser un UUID: " + id);
    }

    @Test
    void propagaElIdentificadorQueEnviaElCliente() {
        given()
                .header("X-Request-Id", "req-1234")
                .when().get("/api/logging/estructurado")
                .then()
                .statusCode(200)
                .header("X-Request-Id", equalTo("req-1234"));
    }

    @Test
    void elLogEstructuradoDevuelveLosDatos() {
        given()
                .when().get("/api/logging/estructurado")
                .then()
                .statusCode(200)
                .body("evento", equalTo("alta_equipo"))
                .body("deporte", equalTo("baloncesto"));
    }
}

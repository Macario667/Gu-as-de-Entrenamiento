package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
class ValidacionResourceTest {

    @Test
    void aceptaUnEventoValido() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Torneo inclusivo",
                          "deporte": "baloncesto",
                          "capacidad": 40
                        }
                        """)
                .when().post("/api/validacion/eventos")
                .then()
                .statusCode(200)
                .body("mensaje", equalTo("Evento validado"))
                .body("capacidad", equalTo(40));
    }

    @Test
    void rechazaNombreEnBlanco() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": " ",
                          "deporte": "baloncesto",
                          "capacidad": 40
                        }
                        """)
                .when().post("/api/validacion/eventos")
                .then()
                .statusCode(400);
    }

    @Test
    void rechazaCapacidadFueraDelRango() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Torneo inclusivo",
                          "deporte": "baloncesto",
                          "capacidad": 0
                        }
                        """)
                .when().post("/api/validacion/eventos")
                .then()
                .statusCode(400);
    }
}

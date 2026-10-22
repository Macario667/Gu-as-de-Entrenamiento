package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class EventoResourceTest {

    @Test
    void listaLosEventosIniciales() {
        given()
                .when().get("/api/eventos")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("[0].nombre", notNullValue());
    }

    @Test
    void filtraPorDeporte() {
        given()
                .queryParam("deporte", "baloncesto")
                .when().get("/api/eventos")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].deporte", equalTo("baloncesto"));
    }

    @Test
    void creaBuscaActualizaYElimina() {
        Long id = given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Maratón inclusivo",
                          "deporte": "atletismo",
                          "capacidad": 50
                        }
                        """)
                .when().post("/api/eventos")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract().jsonPath().getLong("id");

        given()
                .when().get("/api/eventos/{id}", id)
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Maratón inclusivo"))
                .body("capacidad", equalTo(50));

        given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Maratón inclusivo 2026",
                          "deporte": "atletismo",
                          "capacidad": 80
                        }
                        """)
                .when().put("/api/eventos/{id}", id)
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Maratón inclusivo 2026"));

        given()
                .when().delete("/api/eventos/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/api/eventos/{id}", id)
                .then()
                .statusCode(404)
                .body("error", notNullValue());
    }

    @Test
    void devuelveElInformeEnStreaming() {
        given()
                .when().get("/api/informe")
                .then()
                .statusCode(200)
                .body(org.hamcrest.Matchers.containsString("Informe de eventos"));
    }
}

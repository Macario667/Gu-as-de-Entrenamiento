package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class EventoResourceTest {

    @Test
    void listaLosEventosPaginados() {
        given()
                .queryParam("offset", 0)
                .queryParam("limit", 2)
                .when().get("/api/eventos")
                .then()
                .statusCode(200)
                .header("Cache-Control", containsString("max-age=60"))
                .header("X-Total-Count", notNullValue())
                .body("$", org.hamcrest.Matchers.hasSize(2));
    }

    @Test
    void filtraPorDeporte() {
        given()
                .queryParam("deporte", "baloncesto")
                .when().get("/api/eventos/buscar")
                .then()
                .statusCode(200)
                .body("$", org.hamcrest.Matchers.hasSize(1))
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
                .header("X-Created-By", equalTo("EventoResource"))
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
    void elDetalleNoSeCachea() {
        given()
                .when().get("/api/eventos/1/detalle")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    void redirigeConSeeOther() {
        given()
                .redirects().follow(false)
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Copa de swimming",
                          "deporte": "natación",
                          "capacidad": 12
                        }
                        """)
                .when().post("/api/eventos/1/acciones/crear")
                .then()
                .statusCode(303)
                .header("Location", containsString("/api/eventos/"));
    }
}

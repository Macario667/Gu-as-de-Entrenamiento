package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class ActividadResourceTest {

    @Test
    void listaYFiltraActividadesPorDeporte() {
        given()
                .when().get("/api/actividades")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)));

        given()
                .queryParam("deporte", "balon")
                .when().get("/api/actividades")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].deporte", equalTo("baloncesto"));
    }

    @Test
    void completaElCicloDeCreacionConsultaActualizacionYBorrado() {
        Long id = given()
                .contentType("application/json")
                .body("""
                        {"nombre":"Torneo inclusivo","deporte":"voleibol","capacidad":32}
                        """)
                .when().post("/api/actividades")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .extract().jsonPath().getLong("id");

        given()
                .when().get("/api/actividades/{id}", id)
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Torneo inclusivo"));

        given()
                .contentType("application/json")
                .body("""
                        {"nombre":"Torneo 2027","deporte":"voleibol","capacidad":40}
                        """)
                .when().put("/api/actividades/{id}", id)
                .then()
                .statusCode(200)
                .body("capacidad", equalTo(40));

        given()
                .when().delete("/api/actividades/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when().get("/api/actividades/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    void rechazaDatosInvalidosYIdsInexistentes() {
        given()
                .contentType("application/json")
                .body("""
                        {"nombre":" ","deporte":"baloncesto","capacidad":0}
                        """)
                .when().post("/api/actividades")
                .then()
                .statusCode(400)
                .body("error", equalTo("nombre y deporte son obligatorios"));

        given()
                .when().get("/api/actividades/99999")
                .then()
                .statusCode(404);
    }
}

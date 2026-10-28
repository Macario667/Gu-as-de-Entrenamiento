package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest 
class ActividadPanacheResourceTest {
    
    private static final String BASE =
        "/api/mongodb/panache/actividades";

    @Test 
    void crearConsultarYEliminarActividad() {
        String id = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "nombre": "Atletismo inclusivo test",
                  "deporte": "Atletismo",
                  "capacidad": 18
                }
                """)
        .when()
            .post(BASE)
        .then()
            .statusCode(201)
            .body("nombre", equalTo("Atletismo inclusivo test"))
            .extract()
            .path("id");

        given()
        .when()
            .get(BASE + "/" + id)
        .then()
            .statusCode(200)
            .body("deporte", equalTo("Atletismo"));

        given()
        .when()
            .delete(BASE + "/" + id)
        .then()
            .statusCode(204);

        given()
        .when()
            .get(BASE + "/" + id)
        .then()
            .statusCode(404);
    }
    
    @Test
    void rechazarActividadSinNombre() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                "nombre": "",
                "deporte": "Natación",
                "capacidad": 10
                }
                """)
        .when()
            .post(BASE)
        .then()
            .statusCode(400);
    }

    @Test
    void devolver404ParaIdentificadorInexistente() {
        given()
        .when()
            .get(BASE + "/507f1f77bcf86cd799439011")
        .then()
            .statusCode(404);
    }

    @Test
    void rechazarCapacidadInvalida() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                "nombre": "Actividad de prueba",
                "deporte": "Natación",
                "capacidad": 0
                }
                """)
        .when()
            .post(BASE)
        .then()
            .statusCode(400);
    }

}

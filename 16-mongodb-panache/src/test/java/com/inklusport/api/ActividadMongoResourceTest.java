package com.inklusport.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest 
class ActividadMongoResourceTest {
 
    @Inject 
    ObjectMapper objectMapper;

    @Test 
    void debeCrearBuscarYEliminarActividad() throws Exception {
        String cuerpo = """
            {
              "nombre": "Actividad de prueba",
              "deporte": "Baloncesto",
              "capacidad": 12
            }
            """;

        String respuesta = given()
                .contentType("application/json")
                .body(cuerpo)
                .when()
                .post("/api/mongodb/actividades")
                .then()
                .statusCode(201)
                .body("nombre", equalTo("Actividad de prueba"))
                .extract()
                .asString();

        JsonNode json = objectMapper.readTree(respuesta);
        String id = json.get("id").asText();

        given()
                .when()
                .get("/api/mongodb/actividades/" + id)
                .then()
                .statusCode(200)
                .body("deporte", equalTo("Baloncesto"));

        given()
                .when()
                .delete("/api/mongodb/actividades/" + id)
                .then()
                .statusCode(anyOf(is(204), is(200)));

        given()
                .when()
                .get("/api/mongodb/actividades/" + id)
                .then()
                .statusCode(404);
    }
}

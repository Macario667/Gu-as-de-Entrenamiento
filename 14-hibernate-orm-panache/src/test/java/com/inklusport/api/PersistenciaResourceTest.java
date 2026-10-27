package com.inklusport.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest 
class PersistenciaResourceTest {
    
    @Test
    void debeConectarseAInklusport() {
        given()
            .when()
                .get("/api/persistencia/conexion")
            .then()
                .statusCode(200)
                .body("estado", equalTo("conectado"))
                .body("baseDatos", notNullValue())
                .body("resultado", equalTo(1));
    }
}

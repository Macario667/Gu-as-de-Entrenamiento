package com.inklusport.api;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

/**
 * SaludoPerfilTest puede definir propiedades especifícas para
 * las pruebas. Quarkus se encarga de gestionar el entorno correspondiente.
 */

@QuarkusTest 
@TestProfile(PerfilPrueba.class)
public class SaludoPerfilTest {
    
    @Test 
    void debeResponderConElPerfilConfigurado() {
        given()
        .when()
            .get("/saludos")
        .then()
            .statusCode(200)
            .body(is("Hola, Inklusport"));
    }
}

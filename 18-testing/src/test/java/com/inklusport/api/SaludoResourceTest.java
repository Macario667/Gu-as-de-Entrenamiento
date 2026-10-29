
package com.inklusport.api;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.net.URL;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@TestHTTPEndpoint(SaludoResource.class)
class SaludoResourceTest {

    @TestHTTPResource URL url;

    /**
     * given(), when() y then() estructuran la petición y las
     * comprobaciones de REST Assured
     */

    @Test
    void debeSaludarAInklusport() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(200)
            .body(is("Hola, Inklusport"));
    }

    @Test
    void debeSaludarPorNombre() {
        given()
        .when()
            .get("/Ana")
        .then()
            .statusCode(200)
            .body(is("Hola, Ana"));
    }

    @Test
    void debeInformarQueElServicioEstaDisponible() {
        given()
        .when()
            .get("/estado")
        .then()
            .statusCode(200)
            .body(containsString("Servicio disponible"));
    }

    @Test
    void debeInyectarLaUrlDelRecurso() {
        org.junit.jupiter.api.Assertions.assertNotNull(url);
    }
}

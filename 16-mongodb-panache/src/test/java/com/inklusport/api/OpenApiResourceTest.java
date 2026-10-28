package com.inklusport.api;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

@QuarkusTest
class OpenApiResourceTest {

    @Test
    void documentaLaApiYLosEndpointsAcumulados() {
        given()
                .accept("application/json")
                .when().get("/q/openapi")
                .then()
                .statusCode(200)
                .body("info.title", equalTo("InkluSport API"))
                .body("paths", hasKey("/api/documentacion"))
                .body("paths", hasKey("/api/actividades"))
                .body("paths", hasKey("/api/validacion/eventos"))
                .body("paths", hasKey("/api/json-rest/eventos"));
    }

    @Test
    void publicaEnlacesDeOpenApiYSwaggerUi() {
        given()
                .when().get("/api/documentacion")
                .then()
                .statusCode(200)
                .body("openapi", equalTo("/q/openapi"))
                .body("swaggerUi", equalTo("/q/swagger-ui"));
    }
}

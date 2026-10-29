package com.inklusport.api;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;

import com.inklusport.service.SaludoService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest 
class SaludoResourceSpyTest {
    
    @InjectSpy  
    SaludoService service;

    @Test 
    void debeInvocarElServicioConElNombreSolicitado() {
        given()
        .when()
            .get("/saludos/Carlos")
        .then()
            .statusCode(200);

        verify(service).generarSaludo("Carlos");
    }
}

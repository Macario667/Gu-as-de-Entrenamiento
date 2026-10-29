
package com.inklusport.api;

import com.inklusport.service.SaludoService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class SaludoResourceMockTest {

    @InjectMock
    SaludoService saludoService;

    @Test
    void debeUsarLaRespuestaDelServicioSimulado() {
        when(saludoService.generarSaludo("Ana"))
            .thenReturn("Bienvenida a Inklusport, Ana");

        given()
        .when()
            .get("/saludos/Ana")
        .then()
            .statusCode(200)
            .body(is("Bienvenida a Inklusport, Ana"));
    }

    @Test 
    void debeRetornarMensajeDeNoAutorizado() {
        when(saludoService.generarSaludo("Ana"))
            .thenReturn("Usuario no autorizado");

        given()
        .when()
            .get("/saludos/Ana")
        .then()
        .statusCode(200)
        .body(is("Usuario no autorizado"));
    }
}

package com.inklusport.config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import com.inklusport.AppConfig;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ConfigReferenceTest {

    @Inject
    AppConfig config;

    @Test
    void laFuentePropiaAportaSusPropiedades() {
        given()
                .when().get("/config")
                .then()
                .statusCode(200)
                .body("clave", equalTo("app.nombre"))
                .body("valor", notNullValue())
                .body("fuente", notNullValue())
                .body("ordinal", greaterThan(0));
    }

    @Test
    void listaLasFuentesDeConfiguracionOrdenadas() {
        List<Map<String, Object>> fuentes = given()
                .when().get("/config/fuentes")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList(".");

        assertTrue(fuentes.stream()
                .anyMatch(f -> "EntornoConfigSource".equals(f.get("nombre"))));
    }

    @Test
    void convierteLosTiposDePropiedad() {
        given()
                .when().get("/config/tipos")
                .then()
                .statusCode(200)
                .body("duracion", equalTo("PT30M"))
                .body("deportes", hasSize(3))
                .body("entorno", equalTo("local"));
    }

    @Test
    void lasListasYDuracionesSeInyectanTipadas() {
        assertEquals(Duration.ofMinutes(30), config.ventanaReserva());

        List<String> deportes = config.deportes();
        assertEquals(3, deportes.size());
        assertEquals("baloncesto", deportes.get(0));
    }
}

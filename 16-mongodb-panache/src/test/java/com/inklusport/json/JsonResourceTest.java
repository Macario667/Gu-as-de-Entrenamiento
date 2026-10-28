package com.inklusport.json;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class JsonResourceTest {

    @Test
    void muestraLosSeisTiposDeJson() {
        given()
                .when().get("/api/json/estructura")
                .then()
                .statusCode(200)
                .body("tipos.nombre", equalTo("string"))
                .body("tipos.activo", equalTo("boolean"))
                .body("tipos.capacidad", equalTo("number"))
                .body("tipos.descripcion", equalTo("null"))
                .body("tipos.deportes", equalTo("array"))
                .body("tipos.sede", equalTo("object"));
    }

    @Test
    void construyeDocumentosAnidados() {
        given()
                .when().get("/api/json/constructor")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("evento.nombre", equalTo("Torneo inclusivo"))
                .body("evento.deportes", hasSize(2))
                .body("evento.deportes[0]", equalTo("baloncesto"));
    }

    @Test
    void elDocumentoBonitoEsJsonValido() {
        String texto = given()
                .when().get("/api/json/bonito")
                .then()
                .statusCode(200)
                .extract().body().asString();

        org.junit.jupiter.api.Assertions.assertTrue(texto.contains("\"nombre\""));
        org.junit.jupiter.api.Assertions.assertTrue(texto.contains("\n"));
    }

    @Test
    void elDocumentoCompactoNoTieneEspacios() {
        String texto = given()
                .when().get("/api/json/compactar")
                .then()
                .statusCode(200)
                .extract().body().asString();

        org.junit.jupiter.api.Assertions.assertFalse(texto.contains(" "));
    }

    @Test
    void accedeAValoresAnidadosConJsonPointer() {
        given()
                .queryParam("ruta", "/sede/ciudad")
                .when().get("/api/json/puntero")
                .then()
                .statusCode(200)
                .body("valor", equalTo("Valencia"))
                .body("tipo", equalTo("string"));
    }

    @Test
    void parseaTextoJsonPlano() {
        given()
                .contentType("text/plain; charset=UTF-8")
                .body("{\"nombre\":\"Maratón\",\"capacidad\":10}")
                .when().post("/api/json/parsear")
                .then()
                .statusCode(200)
                .body("tipo", equalTo("object"))
                .body("contenido", is(org.hamcrest.Matchers.containsString("Maratón")));
    }

    @Test
    void recibeUnJsonObjectComoCuerpo() {
        given()
                .contentType("application/json")
                .body("{\"nombre\":\"Voley inclusivo\",\"capacidad\":30}")
                .when().post("/api/json/objeto")
                .then()
                .statusCode(200)
                .body("nombre", equalTo("Voley inclusivo"))
                .body("procesado", equalTo(true))
                .body("$", hasKey("id"));
    }

    @Test
    void construyeArrays() {
        given()
                .when().get("/api/json/array")
                .then()
                .statusCode(200)
                .body("homogeneo", hasSize(3))
                .body("homogeneo[1]", equalTo("atletismo"))
                .body("heterogeneo", hasSize(5))
                .body("tamano", equalTo(3));
    }
}

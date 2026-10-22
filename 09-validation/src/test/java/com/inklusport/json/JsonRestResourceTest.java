package com.inklusport.json;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;

@QuarkusTest
class JsonRestResourceTest {

    @Test
    void losDtoRenombranLosCampos() {
        given()
                .when().get("/api/json-rest/eventos")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(3)))
                .body("[0]", hasKey("disciplina"))
                .body("[0]", not(hasKey("deporte")))
                .body("[0].fechaInicio", org.hamcrest.Matchers.matchesPattern("\\d{2}/\\d{2}/\\d{4}"));
    }

    @Test
    void seIgnoranLosCamposDesconocidosDelCuerpo() {
        given()
                .contentType("application/json")
                .body("""
                        {
                          "nombre": "Torneo de voley",
                          "disciplina": "voley",
                          "plazas": 16,
                          "campoInventado": "ignorado"
                        }
                        """)
                .when().post("/api/json-rest/eventos")
                .then()
                .statusCode(200)
                .body("recibido", equalTo("Torneo de voley"))
                .body("disciplina", equalTo("voley"));
    }

    @Test
    void losNulosNoSeEmiten() {
        given()
                .contentType("application/json")
                .body("{\"nombre\": \"Sin plazas\", \"disciplina\": \"petanca\"}")
                .when().post("/api/json-rest/eventos")
                .then()
                .statusCode(200)
                .body("$", not(hasKey("plazas")));
    }

    @Test
    void elSerializadorPropioEscribeElPrecioComoTexto() {
        given()
                .when().get("/api/json-rest/precio")
                .then()
                .statusCode(200)
                .body("precio", equalTo("12.50 EUR"))
                .body("texto", equalTo("12.50 EUR"))
                .body("importe", equalTo(12.5f))
                .body("moneda", equalTo("EUR"));
    }

    @Test
    void seMuestraLaConfiguracionEfectivaDeJackson() {
        given()
                .when().get("/api/json-rest/config")
                .then()
                .statusCode(200)
                .body("escribeFechasComoTimestamp", equalTo(false))
                .body("fallaConPropiedadesDesconocidas", equalTo(false))
                .body("inclusionPorDefecto", org.hamcrest.Matchers.containsString("NON_NULL"));
    }

    @Test
    void sePuedeDevolverJsonCrudo() {
        given()
                .when().get("/api/json-rest/json-crudo")
                .then()
                .statusCode(200)
                .body("mensaje", equalTo("Este JSON se escribe a mano, sin POJOs"))
                .body("anidado.ok", equalTo(true))
                .body("anidado.valores", hasSize(3));
    }
}


package com.inklusport.api;

import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class RecursoEstaticoTest {

    @TestHTTPResource("/prueba.html")
    URL pagina;

    @Test
    void debeServirLaPaginaHTML() throws Exception {
        var conexion = pagina.openConnection();

        try (var lector = new java.io.BufferedReader(
                new java.io.InputStreamReader(
                    conexion.getInputStream(),
                    java.nio.charset.StandardCharsets.UTF_8))) {

            String contenido = lector.lines()
                .reduce("", (a, b) -> a + b);

            assertTrue(contenido.contains("Pruebas de Inklusport"));
            assertTrue(contenido.contains("<title>Inklusport Testing</title>"));
        }

        assertEquals("http", pagina.getProtocol());
    }
}

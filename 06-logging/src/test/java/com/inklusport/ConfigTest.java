package com.inklusport;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @TestProfile permite simular otro entorno (dev, prod, test) sin tocar
 * los ficheros de properties: es la forma de probar la configuración.
 */
@QuarkusTest
@TestProfile(ConfigTest.TestConfig.class)
class ConfigTest {

    @Inject
    AppConfig config;

    @Test
    void testConfigOverride() {
        assertEquals("App de test", config.nombre());
        assertEquals(AppConfig.Modo.TEST, config.modo());
    }

    @Test
    void testExpresionDePropiedades() {
        assertEquals("Hola desde App de test v1.0.0", config.bienvenida());
    }

    public static class TestConfig implements QuarkusTestProfile {
        @Override
        public Map<String, String> getConfigOverrides() {
            return Map.of(
                    "app.nombre", "App de test",
                    "app.modo", "TEST"
            );
        }
    }
}

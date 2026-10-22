package com.inklusport.json;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.quarkus.jackson.ObjectMapperCustomizer;

/**
 * Personalizacion global del ObjectMapper de Quarkus.
 *
 * Un bean que implementa ObjectMapperCustomizer se aplica automaticamente
 * al mapper usado por Quarkus REST (y por el cliente REST de la guia 11).
 */
@Singleton
public class JacksonConfig implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper mapper) {
        // Serializadores propios (Precio, fechas)
        mapper.registerModule(PrecioSerializer.modulo());

        // Soporte de las clases de java.time (LocalDate, Instant...)
        mapper.registerModule(new JavaTimeModule());

        // Fechas como texto ISO-8601, no como numero
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // El mapper acepta campos desconocidos: la API es permisiva
        mapper.disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}

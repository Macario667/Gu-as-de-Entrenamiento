package com.inklusport.json;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import com.inklusport.model.Precio;

/**
 * Serializador personalizado de Precio: escribe "12.50 EUR" en vez de
 * un objeto {"importe":12.5,"moneda":"EUR"}.
 */
public class PrecioSerializer extends JsonSerializer<Precio> {

    @Override
    public void serialize(Precio precio, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        gen.writeString(String.format(Locale.ROOT, "%.2f %s", precio.getImporte(), precio.getMoneda()));
    }

    /**
     * Deserializador counterpart: convierte "12.50 EUR" de nuevo en un Precio.
     */
    public static class Deserializer extends com.fasterxml.jackson.databind.JsonDeserializer<Precio> {

        @Override
        public Precio deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctx) throws IOException {
            String texto = p.getValueAsString();
            if (texto == null) {
                return null;
            }
            String[] partes = texto.trim().split("\\s+");
            double importe = Double.parseDouble(partes[0]);
            String moneda = partes.length > 1 ? partes[1] : "EUR";
            return new Precio(importe, moneda);
        }
    }

    /**
     * Serializador de fechas en formato español corto.
     */
    public static class FechaSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate fecha, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeString(fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
    }

    /**
     * Modulo con los serializadores propios.
     */
    public static SimpleModule modulo() {
        SimpleModule module = new SimpleModule("inklusport");
        module.addSerializer(Precio.class, new PrecioSerializer());
        module.addDeserializer(Precio.class, new Deserializer());
        return module;
    }
}

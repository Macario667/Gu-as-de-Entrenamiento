package com.inklusport.api;

/**
 * Excepción de dominio. El mapper la convierte en una respuesta HTTP 404.
 */
public class EventoNoEncontradoException extends RuntimeException {

    public EventoNoEncontradoException(Long id) {
        super("No existe ningún evento con id " + id);
    }
}

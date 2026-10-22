package com.inklusport.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

import io.smallrye.mutiny.Uni;

import com.inklusport.model.Evento;
import com.inklusport.model.EventoInput;

/**
 * Lógica de negocio en un bean CDI. Los datos viven en memoria:
 * al reiniciar la aplicación se pierden.
 */
@ApplicationScoped
public class EventoService {

    private final Map<Long, Evento> eventos = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong(0);

    public EventoService() {
        crear(new EventoInput("Torneo de baloncesto adaptado", "baloncesto", 20));
        crear(new EventoInput("Carrera inclusiva 5K", "atletismo", 100));
        crear(new EventoInput("Jornada de petanca", "petanca", 16));
    }

    public List<Evento> listar() {
        return List.copyOf(eventos.values());
    }

    public Optional<Evento> buscarPorId(Long id) {
        return Optional.ofNullable(eventos.get(id));
    }

    public List<Evento> buscarPorDeporte(String deporte) {
        return eventos.values().stream()
                .filter(e -> e.deporte != null
                        && e.deporte.toLowerCase().contains(deporte.toLowerCase()))
                .toList();
    }

    public Evento crear(EventoInput input) {
        Long id = ids.incrementAndGet();
        Evento evento = new Evento(id, input.nombre, input.deporte, input.capacidad);
        eventos.put(id, evento);
        return evento;
    }

    public Optional<Evento> actualizar(Long id, EventoInput input) {
        return buscarPorId(id).map(existente -> {
            existente.nombre = input.nombre;
            existente.deporte = input.deporte;
            existente.capacidad = input.capacidad;
            return existente;
        });
    }

    public boolean eliminar(Long id) {
        return eventos.remove(id) != null;
    }

    /**
     * Metodo reactivo: simula una llamada lenta a base de datos y devuelve
     * un Uni (un valor asincrono) en lugar de bloquear el hilo.
     */
    public Uni<Evento> buscarAsync(Long id) {
        return Uni.createFrom().item(() -> buscarPorId(id).orElseThrow())
                .onItem().delayIt().by(java.time.Duration.ofMillis(300));
    }
}

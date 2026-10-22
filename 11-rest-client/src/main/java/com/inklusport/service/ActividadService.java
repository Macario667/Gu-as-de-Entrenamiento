package com.inklusport.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;

import com.inklusport.model.Actividad;
import com.inklusport.model.ActividadInput;

@ApplicationScoped
public class ActividadService {

    private final Map<Long, Actividad> actividades = new ConcurrentHashMap<>();
    private final AtomicLong ids = new AtomicLong();

    public ActividadService() {
        crear(new ActividadInput("Baloncesto adaptado", "baloncesto", 20));
        crear(new ActividadInput("Carrera inclusiva", "atletismo", 100));
        crear(new ActividadInput("Jornada de petanca", "petanca", 16));
    }

    public List<Actividad> listar(String deporte) {
        return actividades.values().stream()
                .filter(actividad -> deporte == null || deporte.isBlank()
                        || actividad.deporte.toLowerCase().contains(deporte.trim().toLowerCase()))
                .sorted(Comparator.comparing(actividad -> actividad.id))
                .toList();
    }

    public Optional<Actividad> buscar(Long id) {
        return Optional.ofNullable(actividades.get(id));
    }

    public Actividad crear(ActividadInput input) {
        Long id = ids.incrementAndGet();
        Actividad actividad = new Actividad(id, input.nombre.trim(), input.deporte.trim(), input.capacidad);
        actividades.put(id, actividad);
        return actividad;
    }

    public Optional<Actividad> actualizar(Long id, ActividadInput input) {
        return buscar(id).map(actividad -> {
            actividad.nombre = input.nombre.trim();
            actividad.deporte = input.deporte.trim();
            actividad.capacidad = input.capacidad;
            return actividad;
        });
    }

    public boolean eliminar(Long id) {
        return actividades.remove(id) != null;
    }
}

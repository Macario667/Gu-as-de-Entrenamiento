package com.inklusport.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.inklusport.model.DisciplinaDto;
import com.inklusport.persistence.entity.Disciplina;
import com.inklusport.persistence.repository.DisciplinaRepository;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest 
class DisciplinaRepositoryTest {

    @Inject 
    DisciplinaRepository repository;

    @Test 
    @TestTransaction
    void debeGuardarYBuscarDisciplina() {
        Disciplina disciplina = new Disciplina(
            "Baloncesto adaptado",
            "Deporte inclusivo",
            true
        );

        repository.persist(disciplina);

        Disciplina encontrada = 
                repository.findById(disciplina.id);

        assertNotNull(encontrada);
        assertEquals("Baloncesto adaptado", encontrada.nombre);
    }

    @Test 
    @TestTransaction
    void debeFiltrarDisciplinasAdaptadas() {
        Disciplina disciplina = new Disciplina(
            "Tenis adaptado",
            "Prueba de filtrado",
            true
        );

        repository.persist(disciplina);

        List<Disciplina> resultados = 
                repository.listarAdaptadas();
        
        assertTrue(resultados.stream().anyMatch(
                d -> d.id.equals(disciplina.id)
        ));
    }

    @Test
    @TestTransaction
    void debeBuscarPorNombre() {
        Disciplina disciplina = new Disciplina(
            "Voleibol inclusivo",
            "Prueba de búsqueda",
            true
        );

        repository.persist(disciplina);

        List<Disciplina> resultados = 
                repository.buscarPorNombre("voleibol");
        
        assertTrue(resultados.stream().anyMatch(
                d -> d.id.equals(disciplina.id)
        ));
    }

    @Test
    @TestTransaction
    void debeOrdenarPorNombre() {
        Disciplina z = new Disciplina(
        "Zumba inclusiva", "Prueba", true);
        Disciplina a = new Disciplina(
        "Atletismo adaptado", "Prueba", true);
    
        repository.persist(z);
        repository.persist(a);

        List<Disciplina> resultados = 
                repository.listarOrdenadasPorNombre();
        
        int posicionA = -1;
        int posicionZ = -1;

        for (int i = 0; i < resultados.size(); i++) {
            if (resultados.get(i).id.equals(a.id)) {
                posicionA = i;
            }
            if (resultados.get(i).id.equals(z.id)) {
                posicionZ = i;
            }
        }

        assertTrue(posicionA >= 0);
        assertTrue(posicionZ >= 0);
        assertTrue(posicionA < posicionZ);
    }

    @Test
    @TestTransaction
    void debePaginarResultados() {
        repository.persist(new Disciplina(
                "Disciplina página A", "Prueba", true));
        repository.persist(new Disciplina(
                "Disciplina página B", "Prueba", true));
        repository.persist(new Disciplina(
                "Disciplina página C", "Prueba", true));

        var consulta = repository.consultarPaginadas(0, 2);

        assertEquals(2, consulta.list().size());
        assertTrue(consulta.count() >= 3);
        assertTrue(consulta.pageCount() >= 2);
    }

    @Test 
    @TestTransaction
    void debeBuscarPorNombreYAdaptada() {
        Disciplina coincidencia = new Disciplina();
        coincidencia.nombre = "Natación inclusiva";
        coincidencia.descripcion = "Actividad acuática";
        coincidencia.adaptada = true;
        coincidencia.persist();

        Disciplina noCoincide = new Disciplina();
        noCoincide.nombre = "Natación recreativa";
        noCoincide.descripcion = "Actividad recreativa";
        noCoincide.adaptada = false;
        noCoincide.persist();

        var resultados = repository.buscarPorNombreYAdaptada(
            "natación", 
            false
        );

        assertEquals(1, resultados.size());
        assertTrue(resultados.stream()
            .anyMatch(d -> d.id.equals(noCoincide.id)));
        assertTrue(resultados.stream()
            .noneMatch(d -> d.id.equals(coincidencia.id)));
    }

    @Test
    @Transactional
    void debeDevolverSoloCoincidenciasPorNombreYAdaptada() {
        Disciplina adaptadaCoincidente1 =
                new Disciplina("Prueba natación adaptada", "desc", true);
        Disciplina adaptadaCoincidente2 =
                new Disciplina("Prueba atletismo adaptado", "desc", true);
        Disciplina noAdaptadaCoincidente =
                new Disciplina("Prueba natación recreativa", "desc", false);
        Disciplina adaptadaNoCoincidente =
                new Disciplina("Fútbol adaptado", "desc", true);

        repository.persist(adaptadaCoincidente1);
        repository.persist(adaptadaCoincidente2);
        repository.persist(noAdaptadaCoincidente);
        repository.persist(adaptadaNoCoincidente);

        List<Disciplina> resultados =
                repository.buscarPorNombreYAdaptada("Prueba", true);

        assertEquals(2, resultados.size());
        assertTrue(resultados.stream().anyMatch(
                d -> d.id.equals(adaptadaCoincidente1.id)));
        assertTrue(resultados.stream().anyMatch(
                d -> d.id.equals(adaptadaCoincidente2.id)));

        repository.delete(adaptadaCoincidente1);
        repository.delete(adaptadaCoincidente2);
        repository.delete(noAdaptadaCoincidente);
        repository.delete(adaptadaNoCoincidente);
    }

    @Test
    @TestTransaction
    void debeActualizarDescripcionPorId() {
        Disciplina d = new Disciplina("Boxeo", "vieja", false);
        repository.persist(d);

        long filas = repository.actualizarDescripcionPorId(d.id, "nueva");
        // Debe haber actualizado exactamente 1 fila
        assertEquals(1, filas);

        // Verificar en BD
        repository.getEntityManager().clear();
        Disciplina actualizado = repository.findById(d.id);
        assertEquals("nueva", actualizado.descripcion);
    }

    @Test
    @TestTransaction
    void debeEliminarSoloAdaptadas() {
        Disciplina d1 = new Disciplina("Natación adaptada", "x", true);
        Disciplina d2 = new Disciplina("Tenis", "y", false);
        repository.persist(d1);
        repository.persist(d2);

        long eliminadas = repository.eliminarAdaptadas();
        // Solo d1 debería eliminarse
        assertEquals(1, eliminadas);
        repository.getEntityManager().clear();
        assertNull(repository.findById(d1.id));
        assertNotNull(repository.findById(d2.id));
    }

    @Test
    @TestTransaction
    void debeContarAdaptadas() {
        repository.persist(new Disciplina("A", "", true));
        repository.persist(new Disciplina("B", "", true));
        repository.persist(new Disciplina("C", "", false));

        long total = repository.contarAdaptadas();
        assertEquals(2, total);
    }

    @Test
    @TestTransaction
    void debeCrearYListarResumenDTO() {
        Disciplina d = new Disciplina("Fútbol inclusivo", "Ejemplo", true);
        repository.persist(d);

        List<DisciplinaDto> dtos = repository.listarResumen();
        assertTrue(dtos.stream().anyMatch(dto -> 
            "Fútbol inclusivo".equals(dto.nombre) && dto.adaptada
        ));
    }

    @Test
    @TestTransaction
    void debeActualizarAdaptadaPorId() {
        Disciplina d = new Disciplina("Tenis adaptado", "Prueba", false);
        repository.persist(d);
        long count = repository.actualizarAdaptadaPorId(d.id, true);
        assertEquals(1, count);
        // Verificamos que quedó marcada como adaptada
        repository.getEntityManager().clear();
        Disciplina mod = repository.findById(d.id);
        assertNotNull(mod);
        assertTrue(mod.adaptada);
    }

    @Test
    @TestTransaction
    void debeEliminarNoAdaptadasYContar() {
        Disciplina a = new Disciplina("A", "", false);
        Disciplina b = new Disciplina("B", "", true);
        repository.persist(a);
        repository.persist(b);
        long elim = repository.eliminarNoAdaptadas();
        assertEquals(1, elim);
        repository.getEntityManager().clear();
        assertNull(repository.findById(a.id));
        assertNotNull(repository.findById(b.id));
        /**
         * Verificación de conteo
         */
        assertEquals(1, repository.contarAdaptadas());
    }

    @Test
    @Transactional
    public void debeContarDisciplinasAdaptadas() {
        Disciplina disciplina = new Disciplina();
        disciplina.nombre = "Prueba adaptada conteo";
        disciplina.descripcion = "Para contar";
        disciplina.adaptada = true;
        disciplina.persist();

        long total = repository.contarAdaptadas();

        assertTrue(total >= 1L);
    }

    @Test
    @TestTransaction
    public void debeEliminarSoloDisciplinasAdaptadas() {
        Disciplina adaptada = new Disciplina();
        adaptada.nombre = "Prueba eliminación adaptada";
        adaptada.descripcion = "Debe eliminarse";
        adaptada.adaptada = true;
        adaptada.persist();

        Disciplina noAdaptada = new Disciplina();
        noAdaptada.nombre = "Prueba conservación no adaptada";
        noAdaptada.descripcion = "Debe conservarse";
        noAdaptada.adaptada = false;
        noAdaptada.persist();

        long eliminadas = repository.eliminarAdaptadas();

        assertTrue(eliminadas >= 1L);
        repository.getEntityManager().clear();
        assertNull(Disciplina.findById(adaptada.id));
        assertNotNull(Disciplina.findById(noAdaptada.id));
    }
}

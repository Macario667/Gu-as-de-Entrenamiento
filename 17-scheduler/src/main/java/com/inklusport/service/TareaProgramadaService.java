package com.inklusport.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class TareaProgramadaService {
    
    private static final Logger LOG =
            Logger.getLogger(TareaProgramadaService.class);

    private volatile String ultimaEjecucion = "Todavía no ejecutada";

    private volatile String ultimoResultado = "PENDIENTE";

    /**
     * Se ejecuta cada 30 segundos
     * Se usa every cuando interesa repetir una tarea a intervalos regulares.
     */
    @Scheduled(every = "{app.scheduler.intervalo}")
    void ejecutarTarea() {
        LOG.info("Tarea programada de Inklusport ejecutada");
    }

    /**
     * Se ejecuta una vez por minuto, cuando el segundo es 0
     * segundos, minutos, horas, día-del-mes, mes, día-de-la-semana
     * 
     * Se usa cron cuando se necesita definir un horario concreto 
     * mediante una expresión
     */
    @Scheduled(cron = "0 * * * * ?")
    void tareaCron() {
        LOG.info("Inklusport: tarea cron ejecutada");
    }

    @Scheduled(
        every = "{app.scheduler.intervalo}",
        concurrentExecution = Scheduled.ConcurrentExecution.SKIP
    )
    void tareaPeriodica() {
        LOG.info("Inicio de la tarea periódica de Inklusport");

        try {
            ejecutarProceso();

            ultimaEjecucion = LocalDateTime.now()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            ultimoResultado = "OK";

            LOG.info("Tarea periódica finalizada correctamente");
        } catch (Exception e) {
            LOG.error("Error al ejecutar la tarea periódica", e);
        }
    }

    private void ejecutarProceso() {
        LOG.debug("Procesando información...");
    }

    public String getUltimaEjecucion() {
        return ultimaEjecucion;
    }

    public String getUltimoResultado() {
        return ultimoResultado;
    }
}

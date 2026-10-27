package com.inklusport;

import java.time.Duration;
import java.util.List;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Configuración tipada (SmallRye Config).
 *
 * @ConfigMapping indica que esta interfaz es el reflejo de un grupo de
 * propiedades: el prefijo "app" se combina con el nombre de cada método.
 *
 *   app.nombre        -> String nombre()
 *   app.autor.nombre  -> String autor().nombre()
 *   app.modo          -> Modo modo()
 *   app.ventana-reserva -> Duration ventanaReserva()
 *   app.deportes      -> List<String> deportes()
 *
 * Los métodos sin valor en properties se pueden definir con @WithDefault.
 */
@ConfigMapping(prefix = "app")
public interface AppConfig {

    String nombre();

    @WithDefault("1.0.0")
    String version();

    @WithDefault("DESARROLLO")
    Modo modo();

    String bienvenida();

    /**
     * Configuración anidada: app.autor.nombre y app.autor.email.
     */
    Autor autor();

    interface Autor {
        String nombre();
        String email();
    }

    /**
     * Tipo Duration: en properties se escribe en formato ISO-8601 (PT30M).
     */
    @WithDefault("PT15M")
    Duration ventanaReserva();

    /**
     * Tipo List: en properties se escribe separada por comas.
     */
    @WithDefault("baloncesto,atletismo,petanca")
    List<String> deportes();

    /**
     * Propiedades que aportan otras fuentes de configuración.
     * El @WithDefault evita que la validación de la configuración falle
     * si esa fuente no está cargada (por ejemplo, en tiempo de compilación).
     */
    @WithDefault("desconocido")
    String entorno();

    @WithDefault("desconocido")
    String despliegue();

    @WithDefault("http://localhost:4317")
    String otelColectorUrl();

    enum Modo {
        DESARROLLO, PRODUCCION, TEST
    }
}

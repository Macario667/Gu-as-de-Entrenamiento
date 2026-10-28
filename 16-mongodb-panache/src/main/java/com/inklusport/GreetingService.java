package com.inklusport;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * Bean CDI: la lógica de negocio no va en el recurso REST.
 *
 * @ApplicationScoped indica que el bean vive durante toda la aplicación
 * y que Quarkus gestiona su ciclo de vida (una única instancia compartida).
 */
@ApplicationScoped
public class GreetingService {

    public String greeting(String name) {
        return "hello " + name;
    }
}

package com.inklusport.service;

import com.inklusport.model.ActividadPanache;
import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class ActividadPanacheRepository implements PanacheMongoRepository<ActividadPanache> {
    
}

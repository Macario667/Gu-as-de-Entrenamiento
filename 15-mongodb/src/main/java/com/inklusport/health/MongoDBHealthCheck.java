package com.inklusport.health;

import org.bson.Document;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import com.mongodb.client.MongoClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@Readiness 
@ApplicationScoped 
public class MongoDBHealthCheck implements HealthCheck {
    
    @Inject 
    MongoClient mongoClient;

    @Override 
    public HealthCheckResponse call() {
        try {
            Document resultado = mongoClient    
                    .getDatabase("admin")
                    .runCommand(new Document("ping", 1));

            return HealthCheckResponse.named("mongodb-connection")
                    .status(resultado.getDouble("ok") == 1.0)
                    .withData("database", "inklusport")
                    .build();
        } catch (Exception e) {
            return HealthCheckResponse.named("mongodb-connection")
                    .down()
                    .withData("error", e.getClass().getSimpleName())
                    .build();
        }
    }
}

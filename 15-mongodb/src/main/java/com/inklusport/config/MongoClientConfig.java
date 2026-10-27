package com.inklusport.config;

import com.mongodb.MongoClientSettings;

import io.quarkus.mongodb.runtime.MongoClientCustomizer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class MongoClientConfig implements MongoClientCustomizer {
    
    @Override
public MongoClientSettings.Builder customize(
        MongoClientSettings.Builder builder) {
    return builder.applicationName("Inklusport");
}
}

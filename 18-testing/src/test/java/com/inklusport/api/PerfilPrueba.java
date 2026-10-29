package com.inklusport.api;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class PerfilPrueba implements QuarkusTestProfile {
    
    @Override 
    public Map<String, String> getConfigOverrides() {
        return Map.of(
            "quarkus.http.test-port", "8181"
        );
    }
}

package com.example.appserver.api;

import com.example.appserver.model.HealthResponse;
import com.example.appserver.resources.SystemResource;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SystemResourceImpl implements SystemResource {

    @Override
    public HealthResponse getHealth(String aplicacionId, String userId, String requestId) {
        HealthResponse health = new HealthResponse();
        health.setStatus("ok");
        return health;
    }
}

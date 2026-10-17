package com.example.appserver.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;

import java.util.HashMap;
import java.util.Map;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApiHeaderValidationFilter implements ContainerRequestFilter {

    private static final String REQUIRED_APPLICATION_ID = "APP1";
    private static final String REQUIRED_USER_ID = "SRV";
    private static final String REQUIRED_REQUEST_ID = "12345678abc";

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        if (path == null || !path.startsWith("api")) {
            return;
        }

        Map<String, String> requiredHeaders = new HashMap<>();
        requiredHeaders.put("aplicacion-id", getHeaderValue(requestContext, "aplicacion-id"));
        requiredHeaders.put("application-id", getHeaderValue(requestContext, "application-id"));
        requiredHeaders.put("user-id", getHeaderValue(requestContext, "user-id"));
        requiredHeaders.put("request-id", getHeaderValue(requestContext, "request-id"));

        String applicationId = getHeaderValue(requestContext, "aplicacion-id");
        if (applicationId == null || applicationId.isBlank()) {
            applicationId = getHeaderValue(requestContext, "application-id");
        }
        String userId = getHeaderValue(requestContext, "user-id");
        String requestId = getHeaderValue(requestContext, "request-id");

        if (!REQUIRED_APPLICATION_ID.equals(applicationId) || !REQUIRED_USER_ID.equals(userId)
                || !REQUIRED_REQUEST_ID.equals(requestId)) {
            requestContext.abortWith(Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", "Headers requeridos faltantes o inválidos",
                            "requiredHeaders", Map.of(
                                    "aplicacion-id", REQUIRED_APPLICATION_ID,
                                    "user-id", REQUIRED_USER_ID,
                                    "request-id", REQUIRED_REQUEST_ID)))
                    .build());
        }
    }

    private String getHeaderValue(ContainerRequestContext requestContext, String name) {
        String value = requestContext.getHeaderString(name);
        if (value != null) {
            return value.trim();
        }
        return null;
    }
}

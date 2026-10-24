package com.example.appserver.api;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Priorities;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.MDC;
import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class ApiHeaderValidationFilter {

    private static final String REQUIRED_APPLICATION_ID = "APP1";
    private static final String REQUIRED_USER_ID = "SRV";
    private static final String REQUEST_ID_MDC_KEY = "requestId";
    private static final String REQUEST_ID_PROPERTY = "requestId";
    private static final String REQUEST_START_TIME_PROPERTY = "requestStartTime";

    @ServerRequestFilter(preMatching = true, priority = Priorities.AUTHENTICATION)
    public Response filterRequest(ContainerRequestContext requestContext) {
        MDC.remove(REQUEST_ID_MDC_KEY);
        String path = getSafePath(requestContext);
        if (!isApiPath(path)) {
            return null;
        }
        requestContext.setProperty(REQUEST_START_TIME_PROPERTY, System.nanoTime());

        String applicationId = getHeaderValue(requestContext, "aplicacion-id");
        if (applicationId == null || applicationId.isBlank()) {
            applicationId = getHeaderValue(requestContext, "application-id");
        }
        String userId = getHeaderValue(requestContext, "user-id");
        String requestId = getHeaderValue(requestContext, "request-id");

        if (!REQUIRED_APPLICATION_ID.equals(applicationId) || !REQUIRED_USER_ID.equals(userId)
                || requestId == null || requestId.isBlank()) {
            Log.warnf("HTTP request rejected method=%s path=%s reason=invalid_required_headers",
                    requestContext.getMethod(), path);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "error", "Headers requeridos faltantes o inválidos",
                            "requiredHeaders", Map.of(
                                    "aplicacion-id", REQUIRED_APPLICATION_ID,
                                    "user-id", REQUIRED_USER_ID,
                                    "request-id", "required non-empty")))
                    .build();
        }

        String safeRequestId = sanitizeRequestId(requestId);
        requestContext.setProperty(REQUEST_ID_PROPERTY, safeRequestId);
        MDC.put(REQUEST_ID_MDC_KEY, safeRequestId);
        Log.infof("HTTP request started requestId=%s method=%s path=%s",
                safeRequestId, requestContext.getMethod(), path);
        return null;
    }

    @ServerResponseFilter(priority = Priorities.AUTHENTICATION)
    public void filterResponse(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
        String path = getSafePath(requestContext);
        if (isApiPath(path)) {
            long startTime = (long) requestContext.getProperty(REQUEST_START_TIME_PROPERTY);
            long durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            String requestId = (String) requestContext.getProperty(REQUEST_ID_PROPERTY);
            int status = responseContext.getStatus();
            String message = "HTTP request completed requestId=%s method=%s path=%s status=%d durationMs=%d";

            if (status >= 500) {
                Log.errorf(message, requestId == null ? "n/a" : requestId,
                        requestContext.getMethod(), path, status, durationMs);
            } else if (status >= 400) {
                Log.warnf(message, requestId == null ? "n/a" : requestId,
                        requestContext.getMethod(), path, status, durationMs);
            } else {
                Log.infof(message, requestId == null ? "n/a" : requestId,
                        requestContext.getMethod(), path, status, durationMs);
            }
        }
        MDC.remove(REQUEST_ID_MDC_KEY);
    }

    private String getSafePath(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        return path == null ? null : path.replaceAll("[\\p{Cntrl}]", "_");
    }

    private String sanitizeRequestId(String requestId) {
        String sanitized = requestId.replaceAll("[\\p{Cntrl}]", "_");
        return sanitized.length() > 128 ? sanitized.substring(0, 128) : sanitized;
    }

    private boolean isApiPath(String path) {
        return path != null && (path.equals("api") || path.startsWith("api/")
                || path.equals("/api") || path.startsWith("/api/"));
    }

    private String getHeaderValue(ContainerRequestContext requestContext, String name) {
        String value = requestContext.getHeaderString(name);
        if (value != null) {
            return value.trim();
        }
        return null;
    }
}

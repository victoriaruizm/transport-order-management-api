package com.vruiz.order.assignment;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

/** Cliente REST hacia driver-service. */
@Component
public class DriverClient {

    public record DriverInfo(UUID id, boolean active) {
    }

    private final RestClient client;

    public DriverClient(RestClient.Builder builder, @Value("${driver-service.url}") String url) {
        this.client = builder.baseUrl(url).build();
    }

    public DriverInfo find(UUID id) {
        try {
            return client.get().uri("/api/drivers/{id}", id).retrieve().body(DriverInfo.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Driver not found: " + id);
        }
    }
}

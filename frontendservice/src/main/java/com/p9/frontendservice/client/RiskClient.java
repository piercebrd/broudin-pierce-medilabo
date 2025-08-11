package com.p9.frontendservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RiskClient {

    public record RiskResponse(Long patientId, String risk, int triggerCount) {}

    private final RestClient rest;

    // application.yml has: gateway.url: http://gateway:8080
    public RiskClient(@Value("${gateway.url}") String gatewayUrl) {
        this.rest = RestClient.builder().baseUrl(gatewayUrl).build();
    }

    public RiskResponse getRisk(Long patientId) {
        return rest.get()
                .uri("/api/risk/{id}", patientId)
                .retrieve()
                .body(RiskResponse.class);
    }
}

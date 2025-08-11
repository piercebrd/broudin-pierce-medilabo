package com.p9.riskservice.clients;

import com.p9.riskservice.dto.PatientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PatientClient {
    private final WebClient web;

    public PatientClient(@Value("${services.patient.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Mono<PatientDto> getById(Long id) {
        return web.get().uri("/patients/{id}", id)
                .retrieve()
                .bodyToMono(PatientDto.class);
    }
}

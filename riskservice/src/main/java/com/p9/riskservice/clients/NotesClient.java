// src/main/java/com/p9/riskservice/clients/NotesClient.java
package com.p9.riskservice.clients;

import com.p9.riskservice.dto.NoteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

@Component
public class NotesClient {
    private final WebClient web;

    public NotesClient(@Value("${services.notes.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    public Flux<NoteDto> getForPatient(Long patientId) {
        return web.get()
                .uri("/api/patients/{patientId}/notes", patientId)
                .retrieve()
                .bodyToFlux(NoteDto.class)
                // if notes-service returns 404 (no notes), treat as empty
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Flux.empty());
    }
}

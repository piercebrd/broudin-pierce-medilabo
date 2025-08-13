// src/main/java/com/p9/riskservice/clients/NotesClient.java
package com.p9.riskservice.clients;

import com.p9.riskservice.dto.NoteDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;

/**
 * Client responsable de la communication avec le microservice Notes.
 * Utilise le WebClient de Spring pour effectuer des requêtes HTTP et récupérer les notes associées aux patients.
 */
@Component
public class NotesClient {

    // Instance de WebClient utilisée pour communiquer avec le service Notes
    private final WebClient web;

    /**
     * Constructeur qui initialise le WebClient avec l’URL de base du service Notes.
     *
     * @param baseUrl URL de base du service Notes, injectée depuis les propriétés de l’application
     */
    public NotesClient(@Value("${services.notes.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Récupère toutes les notes pour un patient donné via son identifiant.
     *
     * @param patientId identifiant du patient dont on souhaite récupérer les notes
     * @return un flux réactif (Flux) de NoteDto ; vide si aucune note n’est trouvée
     */
    public Flux<NoteDto> getForPatient(Long patientId) {
        return web.get()
                .uri("/api/patients/{patientId}/notes", patientId) // Construit l’URI avec l’ID du patient
                .retrieve() // Envoie la requête HTTP GET
                .bodyToFlux(NoteDto.class) // Convertit la réponse en flux de NoteDto
                // Si le service Notes renvoie une erreur HTTP 404 (Not Found), retourne un Flux vide au lieu de lever une exception
                .onErrorResume(WebClientResponseException.NotFound.class, ex -> Flux.empty());
    }
}

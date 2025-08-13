package com.p9.riskservice.clients;

import com.p9.riskservice.dto.PatientDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Client responsable de la communication avec le microservice Patient.
 * Utilise WebClient pour effectuer des appels HTTP et récupérer les informations d’un patient.
 */
@Component
public class PatientClient {

    // Instance de WebClient utilisée pour communiquer avec le service Patient
    private final WebClient web;

    /**
     * Constructeur qui initialise le WebClient avec l’URL de base du service Patient.
     *
     * @param baseUrl URL de base du service Patient, injectée depuis les propriétés de l’application
     */
    public PatientClient(@Value("${services.patient.base-url}") String baseUrl) {
        this.web = WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id identifiant unique du patient
     * @return un Mono contenant l’objet PatientDto correspondant
     */
    public Mono<PatientDto> getById(Long id) {
        return web.get()
                .uri("/patients/{id}", id) // Construit l’URI avec l’ID du patient
                .retrieve() // Envoie la requête HTTP GET
                .bodyToMono(PatientDto.class); // Convertit la réponse en Mono de PatientDto
    }
}

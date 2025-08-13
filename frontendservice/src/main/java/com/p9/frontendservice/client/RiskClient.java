package com.p9.frontendservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client permettant d'appeler le service de calcul du risque via le Gateway.
 * Utilise RestClient pour effectuer les requêtes HTTP.
 */
@Component
public class RiskClient {

    /**
     * Représente la réponse du service de calcul de risque.
     * Contient l'ID du patient, le niveau de risque et le nombre de déclencheurs détectés.
     */
    public record RiskResponse(Long patientId, String risk, int triggerCount) {}

    // Instance de RestClient pour communiquer avec le Gateway
    private final RestClient rest;

    /**
     * Constructeur initialisant le RestClient avec l’URL du Gateway.
     * La propriété gateway.url doit être définie dans application.yml (ex: http://gateway:8080).
     *
     * @param gatewayUrl URL de base du Gateway
     */
    public RiskClient(@Value("${gateway.url}") String gatewayUrl) {
        this.rest = RestClient.builder().baseUrl(gatewayUrl).build();
    }

    /**
     * Appelle le service Risk pour récupérer le niveau de risque d’un patient.
     *
     * @param patientId identifiant unique du patient
     * @return un objet RiskResponse contenant les détails du risque
     */
    public RiskResponse getRisk(Long patientId) {
        return rest.get()
                .uri("/api/risk/{id}", patientId) // Construit l'URL avec l'ID du patient
                .retrieve() // Exécute la requête HTTP GET
                .body(RiskResponse.class); // Convertit la réponse en RiskResponse
    }
}

package com.p9.riskservice.controller;

import com.p9.riskservice.dto.RiskResponse;
import com.p9.riskservice.service.RiskService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * Contrôleur REST pour la gestion des évaluations de risque de diabète.
 * Fournit un point d’accès pour obtenir le niveau de risque d’un patient spécifique.
 */
@RestController
@RequestMapping("/api/risk")
public class RiskController {

    // Service chargé de l’évaluation du risque
    private final RiskService riskService;

    /**
     * Constructeur pour injecter le service d’évaluation du risque.
     *
     * @param riskService instance de RiskService
     */
    public RiskController(RiskService riskService) {
        this.riskService = riskService;
    }

    /**
     * Endpoint pour obtenir le risque de diabète d’un patient.
     *
     * @param patientId identifiant unique du patient
     * @return un Mono contenant la réponse avec le niveau de risque et d’autres informations
     */
    @GetMapping(value = "/{patientId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<RiskResponse> getRisk(@PathVariable Long patientId) {
        return riskService.evaluate(patientId);
    }
}

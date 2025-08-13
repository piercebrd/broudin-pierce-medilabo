package com.p9.riskservice.service;

import com.p9.riskservice.clients.NotesClient;
import com.p9.riskservice.clients.PatientClient;
import com.p9.riskservice.dto.NoteDto;
import com.p9.riskservice.dto.PatientDto;
import com.p9.riskservice.dto.RiskResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service responsable de l’évaluation du risque de diabète pour un patient.
 * Il combine les informations personnelles du patient et ses notes médicales
 * afin de déterminer le niveau de risque.
 */
@Service
public class RiskService {

    // Client pour récupérer les informations du patient
    private final PatientClient patients;

    // Client pour récupérer les notes associées au patient
    private final NotesClient notes;

    /**
     * Constructeur pour injecter les dépendances.
     *
     * @param patients client pour interroger le service Patient
     * @param notes client pour interroger le service Notes
     */
    public RiskService(PatientClient patients, NotesClient notes) {
        this.patients = patients;
        this.notes = notes;
    }

    /**
     * Évalue le risque de diabète pour un patient donné.
     *
     * @param patientId identifiant unique du patient
     * @return un Mono contenant la réponse avec le niveau de risque, le nombre de déclencheurs et l’ID du patient
     */
    public Mono<RiskResponse> evaluate(Long patientId) {
        // Récupération des infos patient
        Mono<PatientDto> p = patients.getById(patientId);

        // Récupération des notes et extraction de leur contenu textuel
        Mono<List<String>> ns = notes.getForPatient(patientId)
                .map(NoteDto::getText) // Récupère uniquement le texte de chaque note
                .collectList();

        // Combine les données patient et notes pour calculer le risque
        return Mono.zip(p, ns).map(t -> {
            PatientDto pd = t.getT1();
            List<String> noteTexts = t.getT2();

            int triggerCount = RiskCalculator.countTriggers(noteTexts); // Compte les déclencheurs distincts
            int age = RiskCalculator.age(pd.getDateOfBirth()); // Calcule l’âge
            String level = RiskCalculator.riskLevel(age, pd.getGender(), triggerCount); // Détermine le niveau de risque

            return new RiskResponse(patientId, level, triggerCount);
        });
    }
}

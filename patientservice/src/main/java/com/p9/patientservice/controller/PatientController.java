package com.p9.patientservice.controller;

import com.p9.patientservice.model.Patient;
import com.p9.patientservice.service.PatientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des patients.
 * Fournit des endpoints CRUD pour créer, lire, mettre à jour et supprimer des patients.
 */
@RestController
@RequestMapping("/patients")
@CrossOrigin
public class PatientController {

    // Service qui gère la logique métier des patients
    private final PatientService patientService;

    /**
     * Constructeur pour injecter le service PatientService.
     *
     * @param patientService service gérant les opérations sur les patients
     */
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    /**
     * Récupère la liste de tous les patients.
     *
     * @return liste des patients
     */
    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    /**
     * Récupère un patient par son identifiant.
     *
     * @param id identifiant unique du patient
     * @return ResponseEntity contenant le patient ou un code 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau patient.
     *
     * @param patient objet Patient à créer
     * @return patient créé
     */
    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.addPatient(patient);
    }

    /**
     * Met à jour un patient existant.
     *
     * @param id identifiant du patient à mettre à jour
     * @param updated objet Patient contenant les nouvelles informations
     * @return ResponseEntity contenant le patient mis à jour ou un code 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient updated) {
        return patientService.update(id, updated)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id identifiant du patient à supprimer
     * @return ResponseEntity avec un code 204 si supprimé ou 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        boolean deleted = patientService.deleteById(id);
        return deleted ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}

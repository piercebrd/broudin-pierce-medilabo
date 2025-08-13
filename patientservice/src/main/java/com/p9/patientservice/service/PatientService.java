package com.p9.patientservice.service;

import com.p9.patientservice.model.Patient;
import com.p9.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service métier pour la gestion des patients.
 * Contient la logique pour récupérer, créer, mettre à jour et supprimer des patients.
 */
@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    /**
     * Récupère la liste complète des patients.
     *
     * @return liste de tous les patients
     */
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    /**
     * Recherche un patient par son identifiant.
     *
     * @param id identifiant du patient
     * @return Optional contenant le patient s'il existe, vide sinon
     */
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    /**
     * Ajoute un nouveau patient à la base de données.
     *
     * @param patient patient à ajouter
     * @return le patient sauvegardé
     */
    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    /**
     * Met à jour les informations d’un patient existant.
     *
     * @param id identifiant du patient à mettre à jour
     * @param updated objet Patient contenant les nouvelles informations
     * @return Optional contenant le patient mis à jour ou vide si non trouvé
     */
    public Optional<Patient> update(Long id, Patient updated) {
        return patientRepository.findById(id).map(existing -> {
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setDateOfBirth(updated.getDateOfBirth());
            existing.setGender(updated.getGender());
            existing.setAddress(updated.getAddress());
            existing.setPhoneNumber(updated.getPhoneNumber());
            return patientRepository.save(existing);
        });
    }

    /**
     * Supprime un patient par son identifiant.
     *
     * @param id identifiant du patient à supprimer
     * @return true si le patient a été supprimé, false sinon
     */
    public boolean deleteById(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

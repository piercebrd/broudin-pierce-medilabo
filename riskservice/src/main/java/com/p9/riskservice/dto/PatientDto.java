package com.p9.riskservice.dto;

import java.time.LocalDate;

/**
 * DTO représentant un patient.
 * Utilisé pour transférer les informations de base d’un patient depuis le microservice Patient.
 */
public class PatientDto {

    // Identifiant unique du patient
    private Long id;

    // Prénom du patient
    private String firstName;

    // Nom de famille du patient
    private String lastName;

    // Date de naissance du patient (doit correspondre au champ dans patient-service)
    private LocalDate dateOfBirth;

    // Genre du patient ("M" ou "F", doit correspondre au champ dans patient-service)
    private String gender;

    // Constructeur par défaut
    public PatientDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}

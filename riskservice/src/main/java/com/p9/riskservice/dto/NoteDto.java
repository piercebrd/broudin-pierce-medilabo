package com.p9.riskservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

/**
 * DTO représentant une note associée à un patient.
 * Utilisé pour transférer les données entre le microservice Notes et le service d’évaluation du risque.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NoteDto {

    // Identifiant unique de la note (String, pas Long)
    private String id;

    // Identifiant du patient auquel la note est liée
    private Long patientId;

    // Contenu textuel de la note
    private String text;

    // Date et heure de création de la note
    private Instant createdAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

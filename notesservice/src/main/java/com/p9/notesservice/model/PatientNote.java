package com.p9.notesservice.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "patient_notes")
@CompoundIndex(name = "patient_created_idx", def = "{'patientId': 1, 'createdAt': -1}")
public class PatientNote {
    @Id
    private String id;

    private Long patientId;
    private String text;        // preserve full text (including newlines)
    private Instant createdAt;  // server timestamp (UTC)

    public PatientNote() {}

    public PatientNote(Long patientId, String text, Instant createdAt) {
        this.patientId = patientId;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

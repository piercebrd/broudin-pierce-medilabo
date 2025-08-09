package com.p9.notesservice.repository;

import com.p9.notesservice.model.PatientNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PatientNoteRepository extends MongoRepository<PatientNote, String> {
    List<PatientNote> findByPatientIdOrderByCreatedAtDesc(Long patientId);
}

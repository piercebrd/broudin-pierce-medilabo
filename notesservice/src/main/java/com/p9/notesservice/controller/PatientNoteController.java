package com.p9.notesservice.controller;

import com.p9.notesservice.dto.NoteDtos;
import com.p9.notesservice.model.PatientNote;
import com.p9.notesservice.repository.PatientNoteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des notes des patients.
 * Permet de lister et d'ajouter des notes pour un patient donné.
 */
@RestController
@RequestMapping("/api/patients/{patientId}/notes")
public class PatientNoteController {

    private final PatientNoteRepository repo;

    /**
     * Constructeur pour injecter le repository.
     *
     * @param repo repository des notes patients
     */
    public PatientNoteController(PatientNoteRepository repo) {
        this.repo = repo;
    }

    /**
     * Récupère la liste des notes pour un patient donné, triées par date de création décroissante.
     *
     * @param patientId identifiant unique du patient
     * @return liste des notes sous forme de NoteResponse
     */
    @GetMapping
    public List<NoteDtos.NoteResponse> list(@PathVariable Long patientId) {
        return repo.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(n -> new NoteDtos.NoteResponse(n.getId(), n.getPatientId(), n.getText(), n.getCreatedAt()))
                .toList();
    }

    /**
     * Crée une nouvelle note pour un patient donné.
     *
     * @param patientId identifiant unique du patient
     * @param req objet contenant le texte de la note
     * @return la note créée sous forme de NoteResponse
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteDtos.NoteResponse create(@PathVariable Long patientId, @Valid @RequestBody NoteDtos.CreateNoteRequest req) {
        var note = new PatientNote(patientId, req.text(), Instant.now());
        var saved = repo.save(note);
        return new NoteDtos.NoteResponse(saved.getId(), saved.getPatientId(), saved.getText(), saved.getCreatedAt());
    }
}

package com.p9.notesservice.controller;

import com.p9.notesservice.dto.NoteDtos;
import com.p9.notesservice.model.PatientNote;
import com.p9.notesservice.repository.PatientNoteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/patients/{patientId}/notes")
public class PatientNoteController {

    private final PatientNoteRepository repo;

    public PatientNoteController(PatientNoteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<NoteDtos.NoteResponse> list(@PathVariable Long patientId) {
        return repo.findByPatientIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(n -> new NoteDtos.NoteResponse(n.getId(), n.getPatientId(), n.getText(), n.getCreatedAt()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NoteDtos.NoteResponse create(@PathVariable Long patientId, @Valid @RequestBody NoteDtos.CreateNoteRequest req) {
        var note = new PatientNote(patientId, req.text(), Instant.now());
        var saved = repo.save(note);
        return new NoteDtos.NoteResponse(saved.getId(), saved.getPatientId(), saved.getText(), saved.getCreatedAt());
    }
}

package com.p9.notesservice.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

public class NoteDtos {
    public record CreateNoteRequest(@NotBlank String text) {}
    public record NoteResponse(String id, Long patientId, String text, Instant createdAt) {}
}

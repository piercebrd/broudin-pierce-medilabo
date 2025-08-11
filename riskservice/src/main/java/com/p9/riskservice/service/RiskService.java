package com.p9.riskservice.service;

import com.p9.riskservice.clients.NotesClient;
import com.p9.riskservice.clients.PatientClient;
import com.p9.riskservice.dto.NoteDto;
import com.p9.riskservice.dto.PatientDto;
import com.p9.riskservice.dto.RiskResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RiskService {
    private final PatientClient patients;
    private final NotesClient notes;

    public RiskService(PatientClient patients, NotesClient notes) {
        this.patients = patients;
        this.notes = notes;
    }

    public Mono<RiskResponse> evaluate(Long patientId) {
        Mono<PatientDto> p = patients.getById(patientId);
        Mono<List<String>> ns = notes.getForPatient(patientId)
                .map(NoteDto::getText)   // field name from your DTO
                .collectList();

        return Mono.zip(p, ns).map(t -> {
            PatientDto pd = t.getT1();
            List<String> noteTexts = t.getT2();
            int triggerCount = RiskCalculator.countTriggers(noteTexts);
            int age = RiskCalculator.age(pd.getDateOfBirth());
            String level = RiskCalculator.riskLevel(age, pd.getGender(), triggerCount);
            return new RiskResponse(patientId, level, triggerCount);
        });
    }
}

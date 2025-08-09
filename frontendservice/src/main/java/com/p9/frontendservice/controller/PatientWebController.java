package com.p9.frontendservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Frontend web controller for Patients + Notes (Sprint 2).
 * Patient service:  /api/patients/**
 * Notes service:    /api/patients/{id}/notes
 */
@Controller
public class PatientWebController {

    // ====== Config ======
    @Value("${gateway.url:http://gateway:8080}")
    private String gatewayBaseUrl; // e.g., http://gateway:8080 (inside Docker)

    private WebClient webClient;

    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayBaseUrl)
                .build();
    }

    // ====== DTOs bound to Thymeleaf ======
    public static class PatientDto {
        public Long id;
        public String firstName;
        public String lastName;
        public String gender;
        public String address;
        public String phoneNumber;
        public LocalDate dateOfBirth;
    }

    public static class NoteResponse {
        public String id;
        public Long patientId;
        public String text;
        public String createdAt; // ISO-8601 string
    }

    // ====== List patients ======
    @GetMapping("/patients")
    public String listPatients(Model model) {
        List<PatientDto> patients = webClient.get()
                .uri("/api/patients")
                .retrieve()
                .bodyToFlux(PatientDto.class)
                .collectList()
                .block();
        model.addAttribute("patients", patients);
        return "patients";
    }

    // ====== New patient form ======
    @GetMapping("/patients/new")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "addPatient";
    }

    // ====== Create patient ======
    @PostMapping(value = "/patients", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPatient(@ModelAttribute PatientDto patient, RedirectAttributes ra) {
        PatientDto created = webClient.post()
                .uri("/api/patients")
                .bodyValue(patient)
                .retrieve()
                .bodyToMono(PatientDto.class)
                .block();
        ra.addFlashAttribute("success", "Patient created (ID " + created.id + ").");
        return "redirect:/patients";
    }

    // ====== View patient details (includes notes) ======
    @GetMapping("/patients/{id}")
    public String getPatient(@PathVariable Long id, Model model) {
        PatientDto patient = webClient.get()
                .uri("/api/patients/{id}", id)
                .retrieve()
                .bodyToMono(PatientDto.class)
                .block();

        List<NoteResponse> notes = webClient.get()
                .uri("/api/patients/{id}/notes", id)
                .retrieve()
                .bodyToFlux(NoteResponse.class)
                .collectList()
                .block();

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        return "patientDetails";
    }

    // ====== Edit page (single mapping!) ======
    @GetMapping("/patients/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        PatientDto patient = webClient.get()
                .uri("/api/patients/{id}", id)
                .retrieve()
                .bodyToMono(PatientDto.class)
                .block();
        model.addAttribute("patient", patient); // name must be "patient"
        return "editPatient";
    }

    // ====== Update patient ======
    @PostMapping(value = "/patients/{id}/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updatePatient(@PathVariable Long id, @ModelAttribute PatientDto form, RedirectAttributes ra) {
        webClient.put()
                .uri("/api/patients/{id}", id)
                .bodyValue(form)
                .retrieve()
                .toBodilessEntity()
                .block();
        ra.addFlashAttribute("success", "Patient updated.");
        return "redirect:/patients/" + id;
    }

    // ====== Delete patient ======
    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes ra) {
        webClient.delete()
                .uri("/api/patients/{id}", id)
                .retrieve()
                .toBodilessEntity()
                .block();
        ra.addFlashAttribute("success", "Patient deleted.");
        return "redirect:/patients";
    }

    // ====== Create a note for a patient (Sprint 2) ======
    @PostMapping(value = "/patients/{id}/notes", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String addNote(@PathVariable Long id,
                          @RequestParam("text") String text,
                          RedirectAttributes ra) {
        if (text == null || text.trim().isEmpty()) {
            ra.addFlashAttribute("noteError", "Note text cannot be empty.");
            return "redirect:/patients/" + id;
        }
        try {
            webClient.post()
                    .uri("/api/patients/{id}/notes", id)
                    .bodyValue(Map.of("text", text))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
            ra.addFlashAttribute("noteSuccess", "Note added.");
        } catch (Exception e) {
            ra.addFlashAttribute("noteError", "Failed to add note.");
        }
        return "redirect:/patients/" + id;
    }
}

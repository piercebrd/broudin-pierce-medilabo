package com.p9.frontendservice.controller;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.p9.frontendservice.client.RiskClient;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur Web gérant l’interface utilisateur pour la gestion des patients.
 * Utilise WebClient pour communiquer avec les microservices via le Gateway.
 */
@Controller
public class PatientWebController {

    private final RiskClient riskClient;

    public PatientWebController(RiskClient riskClient) {
        this.riskClient = riskClient;
    }

    @Value("${gateway.url:http://gateway:8080}")
    private String gatewayBaseUrl;

    private WebClient webClient;

    /**
     * Initialise le WebClient après la construction du contrôleur.
     */
    @PostConstruct
    public void initWebClient() {
        this.webClient = WebClient.builder()
                .baseUrl(gatewayBaseUrl)
                .build();
    }

    // ===== DTOs =====
    @Getter
    @Setter
    public static class PatientDto {
        public Long id;
        public String firstName;
        public String lastName;
        public String gender;
        public String address;
        public String phoneNumber;

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Nécessaire pour le binding du formulaire
        public LocalDate dateOfBirth;

        @Override
        public String toString() {
            return "PatientDto{id=" + id + ", firstName='" + firstName + "', lastName='" + lastName +
                    "', gender='" + gender + "', address='" + address + "', phoneNumber='" + phoneNumber +
                    "', dateOfBirth=" + dateOfBirth + "}";
        }
    }

    public static class NoteResponse {
        public String id;
        public Long patientId;
        public String text;
        public String createdAt;
    }

    // ===== Liste =====
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

    // ===== Formulaire de création =====
    @GetMapping("/patients/new")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "addPatient";
    }

    // ===== Création =====
    @PostMapping(value = "/patients", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String createPatient(@ModelAttribute PatientDto patient, RedirectAttributes ra) {
        try {
            PatientDto created = webClient.post()
                    .uri("/api/patients")
                    .bodyValue(patient)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class).map(msg -> new WebClientResponseException(
                                    "Create failed: " + msg, resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null)))
                    .bodyToMono(PatientDto.class)
                    .block();
            ra.addFlashAttribute("success", "Patient created (ID " + created.id + ").");
            return "redirect:/patients";
        } catch (WebClientResponseException e) {
            ra.addFlashAttribute("error", "Create failed: " + e.getMessage());
            return "redirect:/patients/new";
        }
    }

    // ===== Détails (avec notes) =====
    @GetMapping("/patients/{id}")
    public String getPatient(@PathVariable Long id, Model model) {
        var risk = riskClient.getRisk(id);
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
        model.addAttribute("risk", risk);
        return "patientDetails";
    }

    // ===== Formulaire de modification =====
    @GetMapping("/patients/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            PatientDto patient = webClient.get()
                    .uri("/api/patients/{id}", id)
                    .retrieve()
                    .bodyToMono(PatientDto.class)
                    .block();

            System.out.println("Fetched patient for edit: " + patient.toString());
            model.addAttribute("patient", patient);
            return "editPatient";
        } catch (Exception e) {
            System.err.println("Error fetching patient for edit: " + e.getMessage());
            return "redirect:/patients";
        }
    }

    // ===== Mise à jour =====
    @PostMapping(value = "/patients/{id}/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updatePatient(@PathVariable Long id, @ModelAttribute PatientDto form, RedirectAttributes ra, Model model) {
        System.out.println("Received form data: " + form.toString());

        try {
            form.id = (form.id == null) ? id : form.id;

            webClient.put()
                    .uri("/api/patients/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(form)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            ra.addFlashAttribute("success", "Patient updated.");
            return "redirect:/patients/" + id;

        } catch (WebClientResponseException e) {
            System.err.println("Update failed: " + e.getMessage());
            ra.addFlashAttribute("error", "Update failed: " + e.getMessage());
            model.addAttribute("patient", form);
            return "editPatient";
        } catch (Exception e) {
            System.err.println("Unexpected error during update: " + e.getMessage());
            ra.addFlashAttribute("error", "Update failed due to an unexpected error.");
            model.addAttribute("patient", form);
            return "editPatient";
        }
    }

    // ===== Suppression =====
    @PostMapping("/patients/{id}/delete")
    public String deletePatient(@PathVariable Long id, RedirectAttributes ra) {
        try {
            webClient.delete()
                    .uri("/api/patients/{id}", id)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class).map(msg -> new WebClientResponseException(
                                    "Delete failed: " + msg, resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null)))
                    .toBodilessEntity()
                    .block();
            ra.addFlashAttribute("success", "Patient deleted.");
        } catch (WebClientResponseException e) {
            ra.addFlashAttribute("error", "Delete failed: " + e.getMessage());
        }
        return "redirect:/patients";
    }

    // ===== Ajout de note =====
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
                    .onStatus(HttpStatusCode::isError, resp ->
                            resp.bodyToMono(String.class).map(msg -> new WebClientResponseException(
                                    "Add note failed: " + msg, resp.statusCode().value(), resp.statusCode().toString(),
                                    null, null, null)))
                    .toBodilessEntity()
                    .block();
            ra.addFlashAttribute("noteSuccess", "Note added.");
        } catch (WebClientResponseException e) {
            ra.addFlashAttribute("noteError", "Add note failed: " + e.getMessage());
        }
        return "redirect:/patients/" + id;
    }
}

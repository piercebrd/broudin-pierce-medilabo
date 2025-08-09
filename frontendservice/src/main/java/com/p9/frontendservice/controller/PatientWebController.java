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

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class PatientWebController {

    @Value("${gateway.url:http://gateway:8080}")
    private String gatewayBaseUrl;

    private WebClient webClient;

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

        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // <-- important for form binding
        public LocalDate dateOfBirth;

        @Override public String toString() {
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

    // ===== List =====
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

    // ===== Create form =====
    @GetMapping("/patients/new")
    public String addPatientForm(Model model) {
        model.addAttribute("patient", new PatientDto());
        return "addPatient";
    }

    // ===== Create =====
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

    // ===== Details (with notes) =====
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

    // ===== Edit form (single mapping) =====
    @GetMapping("/patients/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        try {
            PatientDto patient = webClient.get()
                    .uri("/api/patients/{id}", id)
                    .retrieve()
                    .bodyToMono(PatientDto.class)
                    .block();

            // Debug logging
            System.out.println("Fetched patient for edit: " + patient.toString());

            model.addAttribute("patient", patient);
            return "editPatient";
        } catch (Exception e) {
            System.err.println("Error fetching patient for edit: " + e.getMessage());
            return "redirect:/patients";
        }
    }

    // ===== Update =====
    @PostMapping(value = "/patients/{id}/edit", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String updatePatient(@PathVariable Long id, @ModelAttribute PatientDto form, RedirectAttributes ra, Model model) {

        // Debug logging
        System.out.println("Received form data: " + form.toString());

        try {
            // Ensure ID is set
            form.id = (form.id == null) ? id : form.id;

            // Force PUT + JSON to patient-service
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

            // Re-add the patient to the model and return to the edit form
            model.addAttribute("patient", form);
            return "editPatient";
        } catch (Exception e) {
            System.err.println("Unexpected error during update: " + e.getMessage());
            ra.addFlashAttribute("error", "Update failed due to an unexpected error.");

            // Re-add the patient to the model and return to the edit form
            model.addAttribute("patient", form);
            return "editPatient";
        }
    }


    // ===== Delete =====
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

    // ===== Notes =====
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

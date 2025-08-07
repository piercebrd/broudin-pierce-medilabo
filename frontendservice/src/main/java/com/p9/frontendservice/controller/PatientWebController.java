package com.p9.frontendservice.controller;

import com.p9.frontendservice.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Controller
@RequestMapping("/patients")
public class PatientWebController {

    private final RestTemplate restTemplate;

    @Value("${gateway.url}")
    private String gatewayURL;

    public PatientWebController(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    // List all patients
    @GetMapping
    public String listPatients(Model model) {
        ResponseEntity<Patient[]> response = restTemplate.getForEntity(gatewayURL + "/api/patients", Patient[].class);
        model.addAttribute("patients", Arrays.asList(response.getBody()));
        return "patients";
    }

    // Show patient details
    @GetMapping("/{id}")
    public String viewPatient(@PathVariable Long id, Model model) {
        ResponseEntity<Patient> response = restTemplate.getForEntity(gatewayURL + "/api/patients/" + id, Patient.class);
        model.addAttribute("patient", response.getBody());
        return "patientDetails";
    }

    // Show create form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "addPatient";
    }

    // Create patient
    @PostMapping
    public String createPatient(@ModelAttribute Patient patient) {
        restTemplate.postForEntity(gatewayURL + "/api/patients", patient, Patient.class);
        return "redirect:/patients";
    }

    // Show edit form
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        ResponseEntity<Patient> response = restTemplate.getForEntity(gatewayURL + "/api/patients/" + id, Patient.class);
        model.addAttribute("patient", response.getBody());
        return "editPatient";
    }

    // Update patient
    @PostMapping("/{id}")
    public String updatePatient(@PathVariable Long id, @ModelAttribute Patient patient) {
        patient.setId(id); // Ensure the ID is set
        restTemplate.put(gatewayURL + "/api/patients/" + id, patient);
        return "redirect:/patients";
    }

    // Delete patient
    @PostMapping("/{id}/delete")
    public String deletePatient(@PathVariable Long id) {
        restTemplate.delete(gatewayURL + "/api/patients/" + id);
        return "redirect:/patients";
    }
}
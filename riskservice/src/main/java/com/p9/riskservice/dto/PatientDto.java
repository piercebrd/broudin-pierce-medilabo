package com.p9.riskservice.dto;

import java.time.LocalDate;

public class PatientDto {
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth; // match your patient-service field
    private String gender;         // "M" or "F" (match your patient-service)

    public PatientDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}

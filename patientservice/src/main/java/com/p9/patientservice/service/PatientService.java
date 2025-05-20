package com.p9.patientservice.service;

import com.p9.patientservice.model.Patient;
import com.p9.patientservice.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    public Patient addPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Optional<Patient> update(Long id, Patient updated) {
        return patientRepository.findById(id).map(existing -> {
            existing.setFirstName(updated.getFirstName());
            existing.setLastName(updated.getLastName());
            existing.setDateOfBirth(updated.getDateOfBirth());
            existing.setGender(updated.getGender());
            existing.setAddress(updated.getAddress());
            existing.setPhoneNumber(updated.getPhoneNumber());
            return patientRepository.save(existing);
        });
    }

    public boolean deleteById(Long id) {
        if (patientRepository.existsById(id)) {
            patientRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

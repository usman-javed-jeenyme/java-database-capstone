package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return patientService.getPatientDetails(token);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPatient(@Valid @RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();
        if (!service.validatePatient(patient)) {
            response.put("message", "Patient with email or phone already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Patient registered successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put("message", "Error registering patient");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    @GetMapping("/{id}/{user}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id,
                                                   @PathVariable String user,
                                                   @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, user);
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return patientService.getPatientAppointment(id, user, token);
    }

    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
                                                      @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return service.filterPatient(condition, name, token);
    }
}

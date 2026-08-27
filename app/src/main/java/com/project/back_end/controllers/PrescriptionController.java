package com.project.back_end.controllers;

import com.project.back_end.models.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@Valid @RequestBody Prescription prescription,
                                              @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        if (prescription.getAppointmentId() != null) {
            appointmentService.changeStatus(1, prescription.getAppointmentId());
        }
        return prescriptionService.savePrescription(prescription);
    }

    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return prescriptionService.getPrescription(appointmentId);
    }
}

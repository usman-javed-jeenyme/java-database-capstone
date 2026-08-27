package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return appointmentService.getAppointments(date, patientName, token);
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@Valid @RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        Map<String, String> response = new HashMap<>();
        int valid = service.validateAppointment(appointment);
        if (valid == -1) {
            response.put("message", "Doctor not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (valid == 0) {
            response.put("message", "Selected time slot is not available");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        response.put("message", "Error booking appointment");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@Valid @RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                                 @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "patient");
        if (validation.getStatusCode().isError()) {
            return validation;
        }
        return appointmentService.cancelAppointment(id, token);
    }
}

package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String user, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(id);
            List<AppointmentDTO> dtos = appointments.stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
            List<AppointmentDTO> dtos = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Invalid condition or error filtering appointments");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String doctorName, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<AppointmentDTO> dtos = appointmentRepository
                    .filterByDoctorNameAndPatientId(doctorName, patientId)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering by doctor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String doctorName, String condition, Long patientId) {
        Map<String, Object> response = new HashMap<>();
        try {
            int status = "past".equalsIgnoreCase(condition) ? 1 : 0;
            List<AppointmentDTO> dtos = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status)
                    .stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error filtering appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                response.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("patient", patient);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving patient details");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private AppointmentDTO toDto(Appointment a) {
        return new AppointmentDTO(
                a.getId(),
                a.getDoctor() != null ? a.getDoctor().getId() : null,
                a.getDoctor() != null ? a.getDoctor().getName() : null,
                a.getPatient() != null ? a.getPatient().getId() : null,
                a.getPatient() != null ? a.getPatient().getName() : null,
                a.getPatient() != null ? a.getPatient().getEmail() : null,
                a.getPatient() != null ? a.getPatient().getPhone() : null,
                a.getPatient() != null ? a.getPatient().getAddress() : null,
                a.getAppointmentTime(),
                a.getStatus()
        );
    }
}

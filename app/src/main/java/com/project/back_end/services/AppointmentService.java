package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final Service sharedService;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              @Lazy Service sharedService,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.sharedService = sharedService;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
                Optional<Doctor> doctor = doctorRepository.findById(appointment.getDoctor().getId());
                doctor.ifPresent(appointment::setDoctor);
            }
            if (appointment.getPatient() != null && appointment.getPatient().getId() != null) {
                Optional<Patient> patient = patientRepository.findById(appointment.getPatient().getId());
                patient.ifPresent(appointment::setPatient);
            }
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existingOpt = appointmentRepository.findById(appointment.getId());
            if (existingOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Appointment existing = existingOpt.get();
            if (appointment.getPatient() != null && appointment.getPatient().getId() != null
                    && !existing.getPatient().getId().equals(appointment.getPatient().getId())) {
                response.put("message", "Patient mismatch");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            int valid = sharedService.validateAppointment(appointment);
            if (valid == -1) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (valid == 0) {
                response.put("message", "Selected time slot is not available");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            existing.setAppointmentTime(appointment.getAppointmentTime());
            if (appointment.getDoctor() != null && appointment.getDoctor().getId() != null) {
                doctorRepository.findById(appointment.getDoctor().getId()).ifPresent(existing::setDoctor);
            }
            if (appointment.getStatus() >= 0) {
                existing.setStatus(appointment.getStatus());
            }
            appointmentRepository.save(existing);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error updating appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
            if (appointmentOpt.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Appointment appointment = appointmentOpt.get();
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !appointment.getPatient().getId().equals(patient.getId())) {
                response.put("message", "Unauthorized to cancel this appointment");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error cancelling appointment");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public ResponseEntity<Map<String, Object>> getAppointments(String date, String patientName, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractEmail(token);
            Doctor doctor = doctorRepository.findByEmail(email);
            if (doctor == null) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.atTime(LocalTime.MAX);

            List<Appointment> appointments;
            if (patientName == null || patientName.isBlank() || "null".equalsIgnoreCase(patientName)
                    || "-".equals(patientName)) {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctor.getId(), patientName, start, end);
            }

            List<AppointmentDTO> dtos = appointments.stream()
                    .map(a -> new AppointmentDTO(
                            a.getId(),
                            a.getDoctor() != null ? a.getDoctor().getId() : null,
                            a.getDoctor() != null ? a.getDoctor().getName() : null,
                            a.getPatient() != null ? a.getPatient().getId() : null,
                            a.getPatient() != null ? a.getPatient().getName() : null,
                            a.getPatient() != null ? a.getPatient().getEmail() : null,
                            a.getPatient() != null ? a.getPatient().getPhone() : null,
                            a.getPatient() != null ? a.getPatient().getAddress() : null,
                            a.getAppointmentTime(),
                            a.getStatus()))
                    .collect(Collectors.toList());
            response.put("appointments", dtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Error retrieving appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}

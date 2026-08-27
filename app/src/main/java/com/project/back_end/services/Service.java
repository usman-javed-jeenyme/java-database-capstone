package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        response.put("message", "Token valid");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin admin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin existing = adminRepository.findByUsername(admin.getUsername());
            if (existing == null || !existing.getPassword().equals(admin.getPassword())) {
                response.put("message", "Invalid username or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(existing.getUsername());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String time, String specialty) {
        boolean hasName = name != null && !name.isBlank() && !"null".equalsIgnoreCase(name) && !"-".equals(name);
        boolean hasTime = time != null && !time.isBlank() && !"null".equalsIgnoreCase(time) && !"all".equalsIgnoreCase(time);
        boolean hasSpecialty = specialty != null && !specialty.isBlank() && !"null".equalsIgnoreCase(specialty) && !"-".equals(specialty);

        if (hasName && hasTime && hasSpecialty) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        }
        if (hasName && hasSpecialty) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        }
        if (hasName && hasTime) {
            return doctorService.filterDoctorByNameAndTime(name, time);
        }
        if (hasTime && hasSpecialty) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        }
        if (hasName) {
            return doctorService.findDoctorByName(name);
        }
        if (hasSpecialty) {
            return doctorService.filterDoctorBySpecility(specialty);
        }
        if (hasTime) {
            return doctorService.filterDoctorsByTime(time);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null) {
            return -1;
        }
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOpt.isEmpty()) {
            return -1;
        }
        LocalDate date = appointment.getAppointmentTime().toLocalDate();
        LocalTime requested = appointment.getAppointmentTime().toLocalTime();
        List<String> available = doctorService.getDoctorAvailability(appointment.getDoctor().getId(), date);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        boolean match = available.stream().anyMatch(slot -> {
            String start = slot.contains("-") ? slot.split("-")[0].trim() : slot.trim();
            try {
                return LocalTime.parse(start, formatter).equals(requested);
            } catch (Exception e) {
                return false;
            }
        });
        return match ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Patient patient = patientRepository.findByEmail(login.getEmail());
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<?> filterPatient(String condition, String name, String token) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Patient not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            boolean hasCondition = condition != null && !condition.isBlank()
                    && !"null".equalsIgnoreCase(condition) && !"-".equals(condition) && !"all".equalsIgnoreCase(condition);
            boolean hasName = name != null && !name.isBlank()
                    && !"null".equalsIgnoreCase(name) && !"-".equals(name);

            if (hasCondition && hasName) {
                return patientService.filterByDoctorAndCondition(name, condition, patient.getId());
            }
            if (hasCondition) {
                return patientService.filterByCondition(condition, patient.getId());
            }
            if (hasName) {
                return patientService.filterByDoctor(name, patient.getId());
            }
            return patientService.getPatientAppointment(patient.getId(), "patient", token);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error filtering patient appointments");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}

package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) {
            return new ArrayList<>();
        }
        Doctor doctor = doctorOpt.get();
        List<String> availableTimes = doctor.getAvailableTimes() != null
                ? new ArrayList<>(doctor.getAvailableTimes())
                : new ArrayList<>();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        List<LocalDateTime> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end)
                .stream()
                .map(a -> a.getAppointmentTime())
                .collect(Collectors.toList());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return availableTimes.stream()
                .filter(slot -> {
                    String startTime = slot.contains("-") ? slot.split("-")[0].trim() : slot.trim();
                    try {
                        LocalTime slotTime = LocalTime.parse(startTime, timeFormatter);
                        LocalDateTime slotDateTime = LocalDateTime.of(date, slotTime);
                        return booked.stream().noneMatch(b -> b.equals(slotDateTime)
                                || (b.toLocalTime().equals(slotTime) && b.toLocalDate().equals(date)));
                    } catch (Exception e) {
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (doctor.getId() == null || !doctorRepository.existsById(doctor.getId())) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long id) {
        try {
            if (!doctorRepository.existsById(id)) {
                return -1;
            }
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getEmail());
            if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            response.put("message", "Login successful");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameLike(name));
        return response;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        doctors = filterDoctorByTime(doctors, time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String time) {
        if (time == null || time.isBlank() || "null".equalsIgnoreCase(time) || "all".equalsIgnoreCase(time)) {
            return doctors;
        }
        boolean am = time.equalsIgnoreCase("AM");
        return doctors.stream()
                .filter(d -> d.getAvailableTimes() != null && d.getAvailableTimes().stream().anyMatch(slot -> {
                    String start = slot.contains("-") ? slot.split("-")[0].trim() : slot.trim();
                    try {
                        int hour = Integer.parseInt(start.split(":")[0]);
                        return am ? hour < 12 : hour >= 12;
                    } catch (Exception e) {
                        return false;
                    }
                }))
                .collect(Collectors.toList());
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctors = filterDoctorByTime(doctorRepository.findByNameLike(name), time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors",
                doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty));
        return response;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String time) {
        List<Doctor> doctors = filterDoctorByTime(
                doctorRepository.findBySpecialtyIgnoreCase(specialty), time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return response;
    }

    public Map<String, Object> filterDoctorsByTime(String time) {
        List<Doctor> doctors = filterDoctorByTime(doctorRepository.findAll(), time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }
}

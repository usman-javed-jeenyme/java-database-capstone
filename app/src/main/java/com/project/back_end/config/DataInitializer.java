package com.project.back_end.config;

import com.project.back_end.models.Admin;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    public DataInitializer(AdminRepository adminRepository,
                            DoctorRepository doctorRepository,
                            PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @Override
    public void run(String... args) {
        if (adminRepository.findByUsername("admin") == null) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin123");
            adminRepository.save(admin);
        }

        if (doctorRepository.count() == 0) {
            Doctor d1 = new Doctor();
            d1.setName("Alice Carter");
            d1.setSpecialty("cardiologist");
            d1.setEmail("alice.carter@smartcare.com");
            d1.setPassword("doctor123");
            d1.setPhone("1234567890");
            d1.setAvailableTimes(Arrays.asList("09:00-10:00", "10:00-11:00", "14:00-15:00"));
            doctorRepository.save(d1);

            Doctor d2 = new Doctor();
            d2.setName("Brian Lee");
            d2.setSpecialty("dentist");
            d2.setEmail("brian.lee@smartcare.com");
            d2.setPassword("doctor123");
            d2.setPhone("1234567891");
            d2.setAvailableTimes(Arrays.asList("11:00-12:00", "15:00-16:00"));
            doctorRepository.save(d2);
        }

        if (patientRepository.count() == 0) {
            List<Patient> patients = Arrays.asList(
                    createPatient("John Smith", "john.smith@email.com", "patient123", "9876543210", "12 Oak Street"),
                    createPatient("Emily Davis", "emily.davis@email.com", "patient123", "9876543211", "34 Pine Avenue"),
                    createPatient("Michael Brown", "michael.brown@email.com", "patient123", "9876543212", "56 Maple Road"),
                    createPatient("Sarah Wilson", "sarah.wilson@email.com", "patient123", "9876543213", "78 Cedar Lane"),
                    createPatient("David Johnson", "david.johnson@email.com", "patient123", "9876543214", "90 Birch Court")
            );
            patientRepository.saveAll(patients);
        }
    }

    private Patient createPatient(String name, String email, String password, String phone, String address) {
        Patient patient = new Patient();
        patient.setName(name);
        patient.setEmail(email);
        patient.setPassword(password);
        patient.setPhone(phone);
        patient.setAddress(address);
        return patient;
    }
}

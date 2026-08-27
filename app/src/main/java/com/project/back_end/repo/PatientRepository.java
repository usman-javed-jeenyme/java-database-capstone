package com.project.back_end.repo;

import com.project.back_end.models.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for Patient entities.
 * Extends JpaRepository to provide CRUD operations for Patient records.
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Retrieves a patient by their email address using a derived query method.
     *
     * @param email the patient's email
     * @return the matching Patient, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * Retrieves a patient by either email or phone number.
     * Useful for login lookup and duplicate-registration checks.
     *
     * @param email the patient's email
     * @param phone the patient's phone number
     * @return the matching Patient, or null if not found
     */
    Patient findByEmailOrPhone(String email, String phone);
}

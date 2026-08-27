-- Smart Clinic Management System — Schema, Sample Data & Stored Procedures
CREATE DATABASE IF NOT EXISTS cms;
USE cms;

CREATE TABLE IF NOT EXISTS admin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialty VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS doctor_available_times (
  doctor_id BIGINT NOT NULL,
  available_time VARCHAR(255) NOT NULL,
  FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

CREATE TABLE IF NOT EXISTS patient (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(10) NOT NULL,
  address VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS appointment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  doctor_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  appointment_time DATETIME(6) NOT NULL,
  status INT NOT NULL,
  FOREIGN KEY (doctor_id) REFERENCES doctor(id),
  FOREIGN KEY (patient_id) REFERENCES patient(id)
);

INSERT INTO admin (username, password)
SELECT 'admin', 'admin123' FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = 'admin');

INSERT INTO doctor (name, specialty, email, password, phone)
SELECT * FROM (
  SELECT 'Alice Carter' AS name, 'cardiologist' AS specialty, 'alice.carter@smartcare.com' AS email, 'doctor123' AS password, '1234567890' AS phone UNION ALL
  SELECT 'Brian Lee', 'dentist', 'brian.lee@smartcare.com', 'doctor123', '1234567891' UNION ALL
  SELECT 'Carla Nguyen', 'dermatologist', 'carla.nguyen@smartcare.com', 'doctor123', '1234567892'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM doctor LIMIT 1);

INSERT INTO doctor_available_times (doctor_id, available_time)
SELECT d.id, t.slot FROM doctor d
JOIN (
  SELECT 'Alice Carter' AS name, '09:00-10:00' AS slot UNION ALL
  SELECT 'Alice Carter', '10:00-11:00' UNION ALL
  SELECT 'Alice Carter', '14:00-15:00' UNION ALL
  SELECT 'Brian Lee', '11:00-12:00' UNION ALL
  SELECT 'Brian Lee', '15:00-16:00' UNION ALL
  SELECT 'Carla Nguyen', '09:00-10:00' UNION ALL
  SELECT 'Carla Nguyen', '13:00-14:00'
) t ON t.name = d.name
WHERE NOT EXISTS (SELECT 1 FROM doctor_available_times LIMIT 1);

INSERT INTO patient (name, email, password, phone, address)
SELECT * FROM (
  SELECT 'John Smith' AS name, 'john.smith@email.com' AS email, 'patient123' AS password, '9876543210' AS phone, '12 Oak Street' AS address UNION ALL
  SELECT 'Emily Davis', 'emily.davis@email.com', 'patient123', '9876543211', '34 Pine Avenue' UNION ALL
  SELECT 'Michael Brown', 'michael.brown@email.com', 'patient123', '9876543212', '56 Maple Road' UNION ALL
  SELECT 'Sarah Wilson', 'sarah.wilson@email.com', 'patient123', '9876543213', '78 Cedar Lane' UNION ALL
  SELECT 'David Johnson', 'david.johnson@email.com', 'patient123', '9876543214', '90 Birch Court'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM patient LIMIT 1);

INSERT INTO appointment (doctor_id, patient_id, appointment_time, status)
SELECT * FROM (
  SELECT 1 AS doctor_id, 1 AS patient_id, DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 9 HOUR AS appointment_time, 0 AS status UNION ALL
  SELECT 1, 2, DATE_ADD(NOW(), INTERVAL 2 DAY) + INTERVAL 10 HOUR, 0 UNION ALL
  SELECT 2, 3, DATE_ADD(NOW(), INTERVAL 3 DAY) + INTERVAL 11 HOUR, 0 UNION ALL
  SELECT 1, 4, DATE_SUB(NOW(), INTERVAL 10 DAY) + INTERVAL 9 HOUR, 1 UNION ALL
  SELECT 3, 5, DATE_SUB(NOW(), INTERVAL 5 DAY) + INTERVAL 13 HOUR, 1 UNION ALL
  SELECT 1, 3, DATE_SUB(NOW(), INTERVAL 20 DAY) + INTERVAL 14 HOUR, 1 UNION ALL
  SELECT 2, 1, DATE_SUB(NOW(), INTERVAL 15 DAY) + INTERVAL 15 HOUR, 1
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM appointment LIMIT 1);

DROP PROCEDURE IF EXISTS GetDailyAppointmentReportByDoctor;
DELIMITER //
CREATE PROCEDURE GetDailyAppointmentReportByDoctor(IN report_date DATE)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty,
        COUNT(a.id) AS appointment_count
    FROM doctor d
    LEFT JOIN appointment a
        ON a.doctor_id = d.id
       AND DATE(a.appointment_time) = report_date
    GROUP BY d.id, d.name, d.specialty
    ORDER BY appointment_count DESC, d.name ASC;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByMonth;
DELIMITER //
CREATE PROCEDURE GetDoctorWithMostPatientsByMonth(IN report_month INT, IN report_year INT)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty,
        COUNT(DISTINCT a.patient_id) AS patient_count
    FROM doctor d
    INNER JOIN appointment a ON a.doctor_id = d.id
    WHERE MONTH(a.appointment_time) = report_month
      AND YEAR(a.appointment_time) = report_year
    GROUP BY d.id, d.name, d.specialty
    ORDER BY patient_count DESC
    LIMIT 1;
END //
DELIMITER ;

DROP PROCEDURE IF EXISTS GetDoctorWithMostPatientsByYear;
DELIMITER //
CREATE PROCEDURE GetDoctorWithMostPatientsByYear(IN report_year INT)
BEGIN
    SELECT
        d.id AS doctor_id,
        d.name AS doctor_name,
        d.specialty,
        COUNT(DISTINCT a.patient_id) AS patient_count
    FROM doctor d
    INNER JOIN appointment a ON a.doctor_id = d.id
    WHERE YEAR(a.appointment_time) = report_year
    GROUP BY d.id, d.name, d.specialty
    ORDER BY patient_count DESC
    LIMIT 1;
END //
DELIMITER ;

# MySQL Schema Design — Smart Clinic Management System

## Overview

Relational data for admins, doctors, patients, and appointments is stored in **MySQL**. Flexible prescription documents are stored in **MongoDB** (`prescriptions` collection).

## Entity Relationship Diagram (logical)

```
admin (1)
doctor (1) ----< appointment >---- (1) patient
doctor (1) ----< doctor_available_times
appointment (1) .... prescription (MongoDB, by appointmentId)
```

## MySQL Tables

### 1. `admin`

| Column     | Type         | Constraints              | Description                |
|------------|--------------|--------------------------|----------------------------|
| id         | BIGINT       | PK, AUTO_INCREMENT       | Admin primary key          |
| username   | VARCHAR(255) | NOT NULL                 | Login username             |
| password   | VARCHAR(255) | NOT NULL                 | Login password             |

### 2. `doctor`

| Column     | Type         | Constraints              | Description                |
|------------|--------------|--------------------------|----------------------------|
| id         | BIGINT       | PK, AUTO_INCREMENT       | Doctor primary key         |
| name       | VARCHAR(100) | NOT NULL                 | Doctor full name           |
| specialty  | VARCHAR(50)  | NOT NULL                 | Medical specialty          |
| email      | VARCHAR(255) | NOT NULL, UNIQUE         | Login email                |
| password   | VARCHAR(255) | NOT NULL                 | Login password             |
| phone      | VARCHAR(10)  | NOT NULL                 | 10-digit phone             |

### 3. `doctor_available_times` (ElementCollection)

| Column         | Type         | Constraints                         | Description           |
|----------------|--------------|-------------------------------------|-----------------------|
| doctor_id      | BIGINT       | FK → doctor(id), NOT NULL           | Owning doctor         |
| available_time | VARCHAR(255) | NOT NULL                            | Slot e.g. `09:00-10:00` |

### 4. `patient`

| Column     | Type         | Constraints              | Description                |
|------------|--------------|--------------------------|----------------------------|
| id         | BIGINT       | PK, AUTO_INCREMENT       | Patient primary key        |
| name       | VARCHAR(100) | NOT NULL                 | Patient full name          |
| email      | VARCHAR(255) | NOT NULL, UNIQUE         | Login email                |
| password   | VARCHAR(255) | NOT NULL                 | Login password             |
| phone      | VARCHAR(10)  | NOT NULL                 | 10-digit phone             |
| address    | VARCHAR(255) | NOT NULL                 | Patient address            |

### 5. `appointment`

| Column           | Type         | Constraints                         | Description                          |
|------------------|--------------|-------------------------------------|--------------------------------------|
| id               | BIGINT       | PK, AUTO_INCREMENT                  | Appointment primary key              |
| doctor_id        | BIGINT       | FK → doctor(id), NOT NULL           | Assigned doctor                      |
| patient_id       | BIGINT       | FK → patient(id), NOT NULL          | Assigned patient                     |
| appointment_time | DATETIME(6)  | NOT NULL                            | Scheduled date/time                  |
| status           | INT          | NOT NULL                            | `0` = scheduled, `1` = completed     |

## Foreign Key Relationships

- `appointment.doctor_id` → `doctor.id` (**Many appointments → One doctor**)
- `appointment.patient_id` → `patient.id` (**Many appointments → One patient**)
- `doctor_available_times.doctor_id` → `doctor.id` (**One doctor → Many time slots**)

## MongoDB Document — `prescriptions`

| Field          | Type   | Required | Description                          |
|----------------|--------|----------|--------------------------------------|
| _id            | String | Yes      | MongoDB document id                  |
| patientName    | String | Yes      | Patient name (3–100 chars)           |
| appointmentId  | Long   | Yes      | Related MySQL appointment id         |
| medication     | String | Yes      | Medication name                      |
| dosage         | String | Yes      | Dosage instructions                  |
| doctorNotes    | String | No       | Optional notes (max 200 chars)       |

## Sample DDL (reference)

```sql
CREATE DATABASE IF NOT EXISTS cms;
USE cms;

CREATE TABLE admin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE doctor (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  specialty VARCHAR(50) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(10) NOT NULL
);

CREATE TABLE doctor_available_times (
  doctor_id BIGINT NOT NULL,
  available_time VARCHAR(255) NOT NULL,
  FOREIGN KEY (doctor_id) REFERENCES doctor(id)
);

CREATE TABLE patient (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  phone VARCHAR(10) NOT NULL,
  address VARCHAR(255) NOT NULL
);

CREATE TABLE appointment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  doctor_id BIGINT NOT NULL,
  patient_id BIGINT NOT NULL,
  appointment_time DATETIME(6) NOT NULL,
  status INT NOT NULL,
  FOREIGN KEY (doctor_id) REFERENCES doctor(id),
  FOREIGN KEY (patient_id) REFERENCES patient(id)
);
```

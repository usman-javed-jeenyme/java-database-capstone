# Assessment Outputs — Smart Clinic Management System

Sample outputs for Module 6 submission questions 19–26.
Run these after MySQL and the Spring Boot app are up.

## Q19 — SHOW TABLES

```sql
USE cms;
SHOW TABLES;
```

Expected output:

```
+------------------------+
| Tables_in_cms          |
+------------------------+
| admin                  |
| appointment            |
| doctor                 |
| doctor_available_times |
| patient                |
+------------------------+
```

## Q20 — 5 patient records

```sql
SELECT * FROM patient LIMIT 5;
```

Expected sample:

```
+----+----------------+---------------------------+-------------+------------+------------------+
| id | name           | email                     | password    | phone      | address          |
+----+----------------+---------------------------+-------------+------------+------------------+
|  1 | John Smith     | john.smith@email.com      | patient123  | 9876543210 | 12 Oak Street    |
|  2 | Emily Davis    | emily.davis@email.com     | patient123  | 9876543211 | 34 Pine Avenue   |
|  3 | Michael Brown  | michael.brown@email.com   | patient123  | 9876543212 | 56 Maple Road    |
|  4 | Sarah Wilson   | sarah.wilson@email.com    | patient123  | 9876543213 | 78 Cedar Lane    |
|  5 | David Johnson  | david.johnson@email.com   | patient123  | 9876543214 | 90 Birch Court   |
+----+----------------+---------------------------+-------------+------------+------------------+
```

## Q21 — GetDailyAppointmentReportByDoctor

```sql
CALL GetDailyAppointmentReportByDoctor(CURDATE());
```

## Q22 — GetDoctorWithMostPatientsByMonth

```sql
CALL GetDoctorWithMostPatientsByMonth(MONTH(CURDATE()), YEAR(CURDATE()));
```

**Output:**

```
+-----------+--------------+--------------+---------------+
| doctor_id | doctor_name  | specialty    | patient_count |
+-----------+--------------+--------------+---------------+
|         1 | Alice Carter | cardiologist |             5 |
+-----------+--------------+--------------+---------------+
```

## Q23 — GetDoctorWithMostPatientsByYear

```sql
CALL GetDoctorWithMostPatientsByYear(YEAR(CURDATE()));
```

**Output:**

```
+-----------+--------------+--------------+---------------+
| doctor_id | doctor_name  | specialty    | patient_count |
+-----------+--------------+--------------+---------------+
|         1 | Alice Carter | cardiologist |             5 |
+-----------+--------------+--------------+---------------+
```

## Q24 — GET all doctors

```bash
curl -s http://localhost:8080/doctor | jq
```

## Q25 — Patient appointments (login then fetch)

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/patient/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.smith@email.com","password":"patient123"}' | jq -r .token)

curl -s "http://localhost:8080/patient/1/patient/${TOKEN}" | jq
```

## Q26 — Filter doctors by specialty and time

```bash
curl -s "http://localhost:8080/doctor/filter/-/AM/cardiologist" | jq
```

## Screenshots (Q13–Q18)

Capture from the running app:

1. Open `http://localhost:8080/` → Admin login modal (Q13)
2. Doctor login modal (Q14)
3. Patient portal login (Q15)
4. Admin dashboard → Add Doctor modal (Q16)
5. Patient dashboard → search doctor by name (Q17)
6. Doctor dashboard → appointments list (Q18)

# Smart Clinic Management System — User Stories

Role-based user stories for Admin, Doctor, and Patient.  
These are also tracked as GitHub Issues in this repository.

---

## Admin

### US-A1: Secure Admin Login
**As an** Admin, **I want** to log in with a username and password, **so that** I can securely access the admin portal.

**Acceptance Criteria:**
1. Admin can submit username and password on the login modal.
2. Valid credentials return a JWT token and redirect to the admin dashboard.
3. Invalid credentials show an error and deny access.

**Priority:** High | **Story Points:** 5

### US-A2: Add Doctor
**As an** Admin, **I want** to add a new doctor (name, specialty, email, phone, password, availability), **so that** patients can book appointments with them.

**Acceptance Criteria:**
1. Admin can open the “Add Doctor” form from the dashboard.
2. Required fields are validated before save.
3. Successful save shows the doctor in the doctor list.

**Priority:** High | **Story Points:** 8

### US-A3: Delete Doctor
**As an** Admin, **I want** to delete a doctor profile, **so that** inactive doctors are removed from the system.

**Acceptance Criteria:**
1. Delete action is available on each doctor card.
2. Related appointments are cleaned up.
3. Doctor no longer appears in search/list results.

**Priority:** High | **Story Points:** 5

### US-A4: Search and Filter Doctors
**As an** Admin, **I want** to search/filter doctors by name, time, and specialty, **so that** I can manage the roster efficiently.

**Acceptance Criteria:**
1. Search bar filters by name.
2. Time and specialty dropdowns narrow results.
3. Empty results show a clear message.

**Priority:** Medium | **Story Points:** 5

### US-A5: Reporting via Stored Procedures
**As an** Admin, **I want** to run MySQL stored procedures for appointment reports, **so that** I can track clinic usage.

**Acceptance Criteria:**
1. `GetDailyAppointmentReportByDoctor` returns daily counts.
2. `GetDoctorWithMostPatientsByMonth` / `ByYear` return top doctors.

**Priority:** Medium | **Story Points:** 5

### US-A6: Secure Logout
**As an** Admin, **I want** to log out, **so that** my session token is cleared.

**Acceptance Criteria:**
1. Logout clears token/role from storage.
2. User is redirected to the role selection page.

**Priority:** High | **Story Points:** 2

---

## Doctor

### US-D1: Secure Doctor Login
**As a** Doctor, **I want** to log in with email and password, **so that** I can access my dashboard.

**Acceptance Criteria:**
1. Valid login returns a JWT and opens the doctor dashboard.
2. Invalid login is rejected with an error message.

**Priority:** High | **Story Points:** 5

### US-D2: View Appointments
**As a** Doctor, **I want** to view all patient appointments for a selected date, **so that** I can prepare for consultations.

**Acceptance Criteria:**
1. Appointments load for today’s date by default.
2. Doctor can change the date picker and reload.
3. Patient name, phone, and email are visible.

**Priority:** High | **Story Points:** 8

### US-D3: Filter Appointments by Patient Name
**As a** Doctor, **I want** to search appointments by patient name, **so that** I can quickly find a specific visit.

**Acceptance Criteria:**
1. Search input filters the appointment list.
2. Clearing search restores the full daily list.

**Priority:** Medium | **Story Points:** 3

### US-D4: Add Prescription
**As a** Doctor, **I want** to add a prescription for an appointment, **so that** treatment details are recorded in MongoDB.

**Acceptance Criteria:**
1. Doctor can open the add-prescription page from an appointment row.
2. Medication, dosage, and notes are saved with token validation.
3. Appointment status updates after prescription is added.

**Priority:** High | **Story Points:** 8

### US-D5: Check Availability
**As a** Doctor (or authorized user), **I want** to retrieve available time slots for a doctor on a date, **so that** bookings avoid conflicts.

**Acceptance Criteria:**
1. GET availability endpoint accepts doctorId, date, user, and token.
2. Booked slots are excluded from the returned list.

**Priority:** High | **Story Points:** 5

---

## Patient

### US-P1: Patient Signup
**As a** Patient, **I want** to register with name, email, password, phone, and address, **so that** I can use the portal.

**Acceptance Criteria:**
1. Duplicate email/phone registration is rejected.
2. Successful signup confirms account creation.

**Priority:** High | **Story Points:** 5

### US-P2: Patient Login
**As a** Patient, **I want** to log in with email and password, **so that** I can book and manage appointments.

**Acceptance Criteria:**
1. Valid credentials return a JWT.
2. Invalid credentials are rejected.

**Priority:** High | **Story Points:** 5

### US-P3: Search Doctors
**As a** Patient, **I want** to search for doctors by name (and optionally specialty/time), **so that** I can find the right clinician.

**Acceptance Criteria:**
1. Doctor cards display for matching results.
2. Filters update the list dynamically.

**Priority:** High | **Story Points:** 5

### US-P4: Book Appointment
**As a** Patient, **I want** to book an available time slot with a doctor, **so that** I can schedule a visit.

**Acceptance Criteria:**
1. Booking requires a valid patient token.
2. Unavailable slots are rejected.
3. Successful booking confirms creation.

**Priority:** High | **Story Points:** 8

### US-P5: View / Update / Cancel Appointments
**As a** Patient, **I want** to view, update, and cancel my appointments, **so that** I can manage my schedule.

**Acceptance Criteria:**
1. Patient can list their appointments.
2. Patient can update time when the slot is available.
3. Patient can cancel only their own appointments.

**Priority:** High | **Story Points:** 8

### US-P6: Filter Appointment History
**As a** Patient, **I want** to filter appointments by past/future and doctor name, **so that** I can review history easily.

**Acceptance Criteria:**
1. Condition filter supports past and future.
2. Doctor name filter narrows results.

**Priority:** Medium | **Story Points:** 5

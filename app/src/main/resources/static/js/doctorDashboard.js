import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().slice(0, 10);
const token = localStorage.getItem("token");
let patientName = "null";

const searchBar = document.getElementById("searchBar");
if (searchBar) {
  searchBar.addEventListener("input", () => {
    const value = searchBar.value.trim();
    patientName = value ? value : "null";
    loadAppointments();
  });
}

const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");
if (datePicker) {
  datePicker.value = selectedDate;
  datePicker.addEventListener("change", () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}
if (todayButton) {
  todayButton.addEventListener("click", () => {
    selectedDate = new Date().toISOString().slice(0, 10);
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

async function loadAppointments() {
  try {
    const data = await getAllAppointments(selectedDate, patientName, token);
    if (!tableBody) return;
    tableBody.innerHTML = "";
    const appointments = data.appointments || [];
    if (appointments.length === 0) {
      tableBody.innerHTML = `<tr><td colspan="5">No Appointments found for today.</td></tr>`;
      return;
    }
    appointments.forEach((appt) => {
      const patient = {
        id: appt.patientId,
        name: appt.patientName,
        phone: appt.patientPhone,
        email: appt.patientEmail
      };
      tableBody.appendChild(createPatientRow(patient, appt.id, appt.doctorId));
    });
  } catch (error) {
    console.error(error);
    if (tableBody) {
      tableBody.innerHTML = `<tr><td colspan="5">Error loading appointments. Try again later.</td></tr>`;
    }
  }
}

document.addEventListener("DOMContentLoaded", () => {
  loadAppointments();
});

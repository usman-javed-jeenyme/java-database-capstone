import { deleteDoctor } from "../services/doctorServices.js";
import { getPatientData } from "../services/patientServices.js";

export function createDoctorCard(doctor) {
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  const role = localStorage.getItem("userRole");
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const times = document.createElement("p");
  const available = (doctor.availableTimes || []).join(", ") || "N/A";
  times.textContent = `Available: ${available}`;

  infoDiv.append(name, specialization, email, times);

  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  if (role === "admin") {
    const deleteBtn = document.createElement("button");
    deleteBtn.classList.add("dashboard-btn");
    deleteBtn.textContent = "Delete";
    deleteBtn.addEventListener("click", async () => {
      const token = localStorage.getItem("token");
      const result = await deleteDoctor(doctor.id, token);
      alert(result.message);
      if (result.success) {
        card.remove();
      }
    });
    actionsDiv.appendChild(deleteBtn);
  } else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.classList.add("dashboard-btn");
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", () => {
      alert("Please log in before booking an appointment.");
    });
    actionsDiv.appendChild(bookBtn);
  } else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.classList.add("dashboard-btn");
    bookBtn.textContent = "Book Now";
    bookBtn.addEventListener("click", async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }
      const patient = await getPatientData(token);
      if (window.showBookingOverlay) {
        window.showBookingOverlay(doctor, patient);
      } else {
        alert(`Ready to book with Dr. ${doctor.name}`);
      }
    });
    actionsDiv.appendChild(bookBtn);
  }

  card.append(infoDiv, actionsDiv);
  return card;
}

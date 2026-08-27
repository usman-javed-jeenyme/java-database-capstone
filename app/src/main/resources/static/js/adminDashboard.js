import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors, saveDoctor } from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

document.addEventListener("DOMContentLoaded", () => {
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) {
    addBtn.addEventListener("click", () => openModal("addDoctor"));
  }
  loadDoctorCards();

  const searchBar = document.getElementById("searchBar");
  const timeFilter = document.getElementById("timeFilter");
  const specialtyFilter = document.getElementById("specialtyFilter");
  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
  if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
});

async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Failed to load doctors:", error);
  }
}

async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar")?.value || "";
    const time = document.getElementById("timeFilter")?.value || "";
    const specialty = document.getElementById("specialtyFilter")?.value || "";
    const result = await filterDoctors(
      name.trim() || null,
      time || null,
      specialty || null
    );
    const doctors = result.doctors || [];
    if (doctors.length === 0) {
      const content = document.getElementById("content");
      if (content) content.innerHTML = "<p>No doctors found with the given filters.</p>";
      return;
    }
    renderDoctorCards(doctors);
  } catch (error) {
    alert("Error filtering doctors");
  }
}

function renderDoctorCards(doctors) {
  const content = document.getElementById("content");
  if (!content) return;
  content.innerHTML = "";
  doctors.forEach((doctor) => {
    content.appendChild(createDoctorCard(doctor));
  });
}

window.adminAddDoctor = async function adminAddDoctor() {
  const token = localStorage.getItem("token");
  if (!token) {
    alert("Admin token missing. Please log in again.");
    return;
  }
  const name = document.getElementById("doctorName").value;
  const email = document.getElementById("doctorEmail").value;
  const password = document.getElementById("doctorPassword").value;
  const phone = document.getElementById("doctorPhone").value;
  const specialty = document.getElementById("specialization").value;
  const availability = Array.from(document.querySelectorAll('input[name="availability"]:checked'))
    .map((el) => el.value);

  const doctor = { name, email, password, phone, specialty, availableTimes: availability };
  const result = await saveDoctor(doctor, token);
  if (result.success) {
    alert(result.message);
    document.getElementById("modal").style.display = "none";
    window.location.reload();
  } else {
    alert(result.message || "Failed to add doctor");
  }
};

window.openModal = openModal;

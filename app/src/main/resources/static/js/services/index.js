import { openModal } from "../components/modals.js";
import { API_BASE_URL } from "../config/config.js";
import { selectRole } from "../render.js";

const ADMIN_API = API_BASE_URL + "/admin";
const DOCTOR_API = API_BASE_URL + "/doctor";

window.onload = function () {
  const adminBtn = document.getElementById("adminLogin");
  const doctorBtn = document.getElementById("doctorLogin");
  if (adminBtn) {
    adminBtn.addEventListener("click", () => openModal("adminLogin"));
  }
  if (doctorBtn) {
    doctorBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

window.adminLoginHandler = async function adminLoginHandler() {
  try {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const admin = { username, password };
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin)
    });
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("admin");
    } else {
      alert("Invalid admin credentials");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again.");
  }
};

window.doctorLoginHandler = async function doctorLoginHandler() {
  try {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const doctor = { email, password };
    const response = await fetch(`${DOCTOR_API}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });
    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      selectRole("doctor");
    } else {
      alert("Invalid doctor credentials");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again.");
  }
};

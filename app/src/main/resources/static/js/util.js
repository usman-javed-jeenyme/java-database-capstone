function setRole(role) {
  localStorage.setItem("userRole", role);
}

function getRole() {
  return localStorage.getItem("userRole");
}

function clearRole() {
  localStorage.removeItem("userRole");
}

function logout() {
  localStorage.removeItem("token");
  localStorage.removeItem("userRole");
  window.location.href = "/";
}

function logoutPatient() {
  localStorage.removeItem("token");
  localStorage.setItem("userRole", "patient");
  window.location.href = "/pages/patientDashboard.html";
}

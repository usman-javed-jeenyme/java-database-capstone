export function renderHeader() {
  const headerDiv = document.getElementById("header");
  if (!headerDiv) return;

  if (window.location.pathname === "/" || window.location.pathname.endsWith("/index.html")) {
    localStorage.removeItem("userRole");
    headerDiv.innerHTML = `
      <header class="header">
        <div class="logo-section">
          <span class="logo-title">Hospital CMS</span>
        </div>
      </header>`;
    return;
  }

  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  if ((role === "loggedPatient" || role === "admin" || role === "doctor") && !token) {
    localStorage.removeItem("userRole");
    alert("Session expired or invalid login. Please log in again.");
    window.location.href = "/";
    return;
  }

  let headerContent = `
    <header class="header">
      <div class="logo-section">
        <span class="logo-title">Hospital CMS</span>
      </div>
      <nav>`;

  if (role === "admin") {
    headerContent += `
      <button id="addDocBtn" class="adminBtn" onclick="openModal('addDoctor')">Add Doctor</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "doctor") {
    headerContent += `
      <button class="adminBtn" onclick="selectRole('doctor')">Home</button>
      <a href="#" onclick="logout()">Logout</a>`;
  } else if (role === "patient") {
    headerContent += `
      <button class="adminBtn" onclick="openModal('patientLogin')">Login</button>
      <button class="adminBtn" onclick="openModal('patientSignup')">Sign Up</button>`;
  } else if (role === "loggedPatient") {
    headerContent += `
      <button class="adminBtn" onclick="window.location.href='/pages/loggedPatientDashboard.html'">Home</button>
      <button class="adminBtn" onclick="window.location.href='/pages/patientAppointments.html'">Appointments</button>
      <a href="#" onclick="logoutPatient()">Logout</a>`;
  }

  headerContent += `</nav></header>`;
  headerDiv.innerHTML = headerContent;
}

window.renderHeader = renderHeader;

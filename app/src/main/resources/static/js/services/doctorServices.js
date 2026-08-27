import { API_BASE_URL } from "../config/config.js";

const DOCTOR_API = API_BASE_URL + "/doctor";

export async function getDoctors() {
  try {
    const response = await fetch(DOCTOR_API);
    const data = await response.json();
    return data.doctors || [];
  } catch (error) {
    console.error("Error fetching doctors:", error);
    return [];
  }
}

export async function deleteDoctor(id, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${id}/${token}`, {
      method: "DELETE"
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Request completed"
    };
  } catch (error) {
    console.error("Error deleting doctor:", error);
    return { success: false, message: "Failed to delete doctor" };
  }
}

export async function saveDoctor(doctor, token) {
  try {
    const response = await fetch(`${DOCTOR_API}/${token}`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor)
    });
    const data = await response.json();
    return {
      success: response.ok,
      message: data.message || "Request completed"
    };
  } catch (error) {
    console.error("Error saving doctor:", error);
    return { success: false, message: "Failed to save doctor" };
  }
}

export async function filterDoctors(name, time, specialty) {
  try {
    const n = name && name.trim() ? name.trim() : "-";
    const t = time && time.trim() ? time.trim() : "all";
    const s = specialty && specialty.trim() ? specialty.trim() : "-";
    const response = await fetch(`${DOCTOR_API}/filter/${encodeURIComponent(n)}/${encodeURIComponent(t)}/${encodeURIComponent(s)}`);
    if (!response.ok) {
      console.error("Filter doctors failed");
      return { doctors: [] };
    }
    return await response.json();
  } catch (error) {
    console.error("Error filtering doctors:", error);
    alert("Unable to filter doctors. Please try again.");
    return { doctors: [] };
  }
}

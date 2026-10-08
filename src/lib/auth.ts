const API_URL = (import.meta.env["VITE_API_URL"] || "http://localhost:8080").replace(/\/+$/, "");

export interface AdminSession {
  token: string;
  name: string;
  email: string;
}

export async function loginAdmin(email: string, password: string): Promise<AdminSession> {
  const response = await fetch(`${API_URL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });
  if (!response.ok) {
    throw new Error("Invalid admin email or password.");
  }
  return response.json() as Promise<AdminSession>;
}

export function getAdminToken(): string | null {
  return window.localStorage.getItem("maison-verre-admin-token");
}

export function saveAdminSession(session: AdminSession): void {
  window.localStorage.setItem("maison-verre-admin-token", session.token);
  window.localStorage.setItem("maison-verre-admin-name", session.name);
}

export function clearAdminSession(): void {
  window.localStorage.removeItem("maison-verre-admin-token");
  window.localStorage.removeItem("maison-verre-admin-name");
}

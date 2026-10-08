import type { Reservation } from "./reservations";

export interface RestaurantTable {
  id: string;
  tableNumber: string;
  capacity: number;
  status: string;
}

export interface RestaurantTableInput {
  tableNumber: string;
  capacity: number;
  status: string;
}

const API_URL = (import.meta.env["VITE_API_URL"] || "http://localhost:8080").replace(
  /\/+$/,
  "",
);

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const token = typeof window === "undefined" ? null : window.localStorage.getItem("maison-verre-admin-token");
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  });
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) {
      throw new Error("Your admin session has expired. Please sign in again.");
    }
    let message = `Request failed (${response.status})`;
    try {
      const body = (await response.json()) as { detail?: string; message?: string };
      message = body.detail || body.message || message;
    } catch {
      // Preserve the HTTP error when the server has no JSON response.
    }
    throw new Error(message);
  }
  return response.status === 204 ? (undefined as T) : (response.json() as Promise<T>);
}

export const loadAdminReservations = () => request<Reservation[]>("/api/reservations");
export const cancelAdminReservation = (id: string) =>
  request<void>(`/api/reservations/${encodeURIComponent(id)}`, { method: "DELETE" });
export const loadTables = () => request<RestaurantTable[]>("/api/tables");
export const createTable = (input: RestaurantTableInput) =>
  request<RestaurantTable>("/api/tables", { method: "POST", body: JSON.stringify(input) });
export const updateTable = (id: string, input: RestaurantTableInput) =>
  request<RestaurantTable>(`/api/tables/${encodeURIComponent(id)}`, {
    method: "PUT",
    body: JSON.stringify(input),
  });
export const deleteTable = (id: string) =>
  request<void>(`/api/tables/${encodeURIComponent(id)}`, { method: "DELETE" });

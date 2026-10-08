export interface Reservation {
  id: string;
  reference: string;
  name: string;
  phone: string;
  date: string;
  time: string;
  partySize: number;
  seating: string;
  requests?: string | undefined;
  createdAt: string;
}

export interface CreateReservationInput {
  name: string;
  phone: string;
  date: string;
  time: string;
  partySize: number;
  seating: string;
  requests?: string;
}

const API_URL = (import.meta.env["VITE_API_URL"] || "http://localhost:8080").replace(
  /\/+$/,
  "",
);

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      ...init?.headers,
    },
  });

  if (!response.ok) {
    let message = `Request failed (${response.status})`;
    try {
      const body = (await response.json()) as { message?: string; error?: string };
      message = body.message || body.error || message;
    } catch {
      // Keep the HTTP status when the server does not return JSON.
    }
    throw new Error(message);
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export function loadReservations(): Promise<Reservation[]> {
  return request<Reservation[]>("/api/reservations");
}

export function createReservation(input: CreateReservationInput): Promise<Reservation> {
  return request<Reservation>("/api/reservations", {
    method: "POST",
    body: JSON.stringify(input),
  });
}

export function cancelReservation(id: string): Promise<void> {
  return request<void>(`/api/reservations/${encodeURIComponent(id)}`, {
    method: "DELETE",
  });
}

export function formatDateLong(iso: string): string {
  const d = new Date(`${iso}T12:00:00`);
  return d.toLocaleDateString("en-US", {
    weekday: "short",
    month: "short",
    day: "numeric",
  });
}

export function formatTime(time: string): string {
  const [h = 0, m = 0] = time.split(":").map(Number);
  const ampm = h >= 12 ? "PM" : "AM";
  const hour = h % 12 === 0 ? 12 : h % 12;
  return `${hour}:${String(m).padStart(2, "0")} ${ampm}`;
}

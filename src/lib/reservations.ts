export interface Reservation {
  id: string;
  reference: string;
  name: string;
  phone: string;
  date: string; // ISO yyyy-mm-dd
  time: string; // e.g. "19:30"
  partySize: number;
  seating: string;
  requests?: string | undefined;
  createdAt: number;
}

const STORAGE_KEY = "maison-verre-reservations";

export function loadReservations(): Reservation[] {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export function saveReservations(reservations: Reservation[]) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(reservations));
}

export function makeReference(): string {
  const n = Math.floor(1000 + Math.random() * 9000);
  return `MV-${n}`;
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

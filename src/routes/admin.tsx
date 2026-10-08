import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";
import { clearAdminSession, getAdminToken, loginAdmin, saveAdminSession } from "../lib/auth";
import {
  cancelAdminReservation,
  createTable,
  deleteTable,
  loadAdminReservations,
  loadTables,
  type RestaurantTable,
  type RestaurantTableInput,
  updateTable,
} from "../lib/admin";
import { formatDateLong, formatTime, type Reservation } from "../lib/reservations";

export const Route = createFileRoute("/admin")({
  head: () => ({ meta: [{ title: "Maison Verre — Admin Dashboard" }] }),
  component: AdminDashboard,
});

const emptyTable: RestaurantTableInput = {
  tableNumber: "",
  capacity: 2,
  status: "AVAILABLE",
};

function AdminDashboard() {
  const [token, setToken] = useState<string | null>(null);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loginError, setLoginError] = useState("");
  const [loggingIn, setLoggingIn] = useState(false);
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [tables, setTables] = useState<RestaurantTable[]>([]);
  const [tableForm, setTableForm] = useState(emptyTable);
  const [editingId, setEditingId] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [savingTable, setSavingTable] = useState(false);
  const [busyId, setBusyId] = useState<string | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    setToken(getAdminToken());
  }, []);

  async function refresh() {
    setLoading(true);
    setError("");
    try {
      const [reservationData, tableData] = await Promise.all([
        loadAdminReservations(),
        loadTables(),
      ]);
      setReservations(reservationData);
      setTables(tableData);
    } catch (cause: unknown) {
      setError(cause instanceof Error ? cause.message : "Unable to load dashboard data.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    if (token) void refresh();
  }, [token]);

  const today = new Date().toISOString().slice(0, 10);
  const upcoming = useMemo(
    () => reservations.filter((reservation) => reservation.date >= today),
    [reservations, today],
  );
  const availableTables = tables.filter((table) => table.status === "AVAILABLE").length;

  function startEdit(table: RestaurantTable) {
    setEditingId(table.id);
    setTableForm({
      tableNumber: table.tableNumber,
      capacity: table.capacity,
      status: table.status,
    });
  }

  function resetTableForm() {
    setEditingId(null);
    setTableForm(emptyTable);
  }

  async function saveTable(event: React.FormEvent) {
    event.preventDefault();
    setSavingTable(true);
    setError("");
    try {
      const saved = editingId
        ? await updateTable(editingId, tableForm)
        : await createTable(tableForm);
      setTables((current) =>
        editingId
          ? current.map((table) => (table.id === saved.id ? saved : table))
          : [...current, saved],
      );
      resetTableForm();
    } catch (cause: unknown) {
      setError(cause instanceof Error ? cause.message : "Unable to save table.");
    } finally {
      setSavingTable(false);
    }
  }

  async function removeTable(id: string) {
    setBusyId(id);
    setError("");
    try {
      await deleteTable(id);
      setTables((current) => current.filter((table) => table.id !== id));
    } catch (cause: unknown) {
      setError(cause instanceof Error ? cause.message : "Unable to delete table.");
    } finally {
      setBusyId(null);
    }
  }

  async function removeReservation(reservation: Reservation) {
    setBusyId(reservation.id);
    setError("");
    try {
      await cancelAdminReservation(reservation.id);
      setReservations((current) => current.filter((item) => item.id !== reservation.id));
    } catch (cause: unknown) {
      setError(cause instanceof Error ? cause.message : "Unable to cancel reservation.");
    } finally {
      setBusyId(null);
    }
  }

  async function signIn(event: React.FormEvent) {
    event.preventDefault();
    setLoggingIn(true);
    setLoginError("");
    try {
      const session = await loginAdmin(email, password);
      saveAdminSession(session);
      setToken(session.token);
      setPassword("");
    } catch (cause: unknown) {
      setLoginError(cause instanceof Error ? cause.message : "Unable to sign in.");
    } finally {
      setLoggingIn(false);
    }
  }

  if (!token) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-background px-6 text-foreground">
        <form onSubmit={signIn} className="w-full max-w-md rounded-2xl border border-white/70 bg-white/70 p-7 shadow-sm">
          <Link to="/" className="text-sm font-medium text-sage-deep hover:underline">← Back to reservations</Link>
          <p className="mt-8 text-xs font-semibold uppercase tracking-[0.2em] text-sage-deep">Maison Verre</p>
          <h1 className="mt-2 font-display text-4xl">Admin sign in</h1>
          <p className="mt-2 text-sm text-foreground/60">Sign in to manage reservations and restaurant tables.</p>
          {loginError && <p className="mt-5 rounded-xl border border-destructive/20 bg-destructive/5 px-4 py-3 text-sm text-destructive">{loginError}</p>}
          <label className="mt-6 block text-sm font-medium">
            Email
            <input required type="email" value={email} onChange={(event) => setEmail(event.target.value)} className="mt-2 w-full rounded-xl border border-foreground/10 bg-white/70 px-3 py-2.5 outline-none focus:border-sage" />
          </label>
          <label className="mt-4 block text-sm font-medium">
            Password
            <input required type="password" value={password} onChange={(event) => setPassword(event.target.value)} className="mt-2 w-full rounded-xl border border-foreground/10 bg-white/70 px-3 py-2.5 outline-none focus:border-sage" />
          </label>
          <button disabled={loggingIn} className="mt-6 w-full rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground disabled:opacity-60">
            {loggingIn ? "Signing in…" : "Sign in"}
          </button>
        </form>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-background px-6 py-8 text-foreground">
      <div className="mx-auto max-w-6xl">
        <header className="flex flex-col gap-4 border-b border-foreground/10 pb-6 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <Link to="/" className="text-sm font-medium text-sage-deep hover:underline">
              ← Back to reservations
            </Link>
            <p className="mt-5 text-xs font-semibold uppercase tracking-[0.2em] text-sage-deep">
              Maison Verre
            </p>
            <h1 className="mt-2 font-display text-4xl">Operations dashboard</h1>
            <p className="mt-2 text-sm text-foreground/60">
              Manage today&apos;s bookings and your dining room tables.
            </p>
          </div>
          <button
            type="button"
            onClick={() => void refresh()}
            className="rounded-xl border border-foreground/10 bg-white/70 px-4 py-2 text-sm font-medium hover:bg-white"
          >
            Refresh data
          </button>
          <button type="button" onClick={() => { clearAdminSession(); setToken(null); }} className="text-sm font-medium text-foreground/60 hover:text-foreground">
            Sign out
          </button>
        </header>

        {error && (
          <p className="mt-5 rounded-xl border border-destructive/20 bg-destructive/5 px-4 py-3 text-sm text-destructive">
            {error}
          </p>
        )}

        <section className="mt-8 grid gap-4 sm:grid-cols-3">
          {[
            ["Upcoming reservations", upcoming.length],
            ["Dining room tables", tables.length],
            ["Available tables", availableTables],
          ].map(([label, value]) => (
            <div key={label} className="rounded-2xl border border-white/70 bg-white/65 p-5 shadow-sm">
              <p className="text-sm text-foreground/50">{label}</p>
              <p className="mt-2 font-display text-3xl">{value}</p>
            </div>
          ))}
        </section>

        <div className="mt-8 grid gap-8 lg:grid-cols-[1.1fr_0.9fr]">
          <section className="rounded-2xl border border-white/70 bg-white/65 p-5 shadow-sm">
            <div className="flex items-center justify-between">
              <h2 className="font-display text-2xl">Reservations</h2>
              <span className="text-sm text-foreground/50">{upcoming.length} upcoming</span>
            </div>
            {loading ? (
              <p className="mt-6 text-sm text-foreground/50">Loading reservations…</p>
            ) : upcoming.length === 0 ? (
              <p className="mt-6 text-sm text-foreground/50">No upcoming reservations.</p>
            ) : (
              <div className="mt-5 space-y-3">
                {upcoming.map((reservation) => (
                  <div key={reservation.id} className="rounded-xl border border-foreground/10 bg-white/60 p-4">
                    <div className="flex items-start justify-between gap-4">
                      <div>
                        <p className="font-medium">
                          {formatDateLong(reservation.date)} · {formatTime(reservation.time)}
                        </p>
                        <p className="mt-1 text-sm text-foreground/60">
                          {reservation.name} · {reservation.partySize} guests · {reservation.seating}
                        </p>
                        <p className="mt-1 text-xs text-foreground/40">
                          {reservation.phone} · Ref {reservation.reference}
                        </p>
                      </div>
                      <button
                        type="button"
                        disabled={busyId === reservation.id}
                        onClick={() => void removeReservation(reservation)}
                        className="text-xs font-medium text-destructive/70 hover:text-destructive"
                      >
                        {busyId === reservation.id ? "Cancelling…" : "Cancel"}
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          <section className="rounded-2xl border border-white/70 bg-white/65 p-5 shadow-sm">
            <h2 className="font-display text-2xl">{editingId ? "Edit table" : "Add table"}</h2>
            <form onSubmit={saveTable} className="mt-5 space-y-3">
              <input
                required
                value={tableForm.tableNumber}
                onChange={(event) => setTableForm({ ...tableForm, tableNumber: event.target.value })}
                placeholder="Table number (e.g. T-02)"
                className="w-full rounded-xl border border-foreground/10 bg-white/70 px-3 py-2.5 text-sm outline-none focus:border-sage"
              />
              <input
                required
                min={1}
                max={50}
                type="number"
                value={tableForm.capacity}
                onChange={(event) => setTableForm({ ...tableForm, capacity: Number(event.target.value) })}
                className="w-full rounded-xl border border-foreground/10 bg-white/70 px-3 py-2.5 text-sm outline-none focus:border-sage"
              />
              <select
                value={tableForm.status}
                onChange={(event) => setTableForm({ ...tableForm, status: event.target.value })}
                className="w-full rounded-xl border border-foreground/10 bg-white/70 px-3 py-2.5 text-sm outline-none focus:border-sage"
              >
                <option value="AVAILABLE">Available</option>
                <option value="OCCUPIED">Occupied</option>
                <option value="UNAVAILABLE">Unavailable</option>
              </select>
              <div className="flex gap-2">
                <button
                  disabled={savingTable}
                  className="flex-1 rounded-xl bg-primary px-4 py-2.5 text-sm font-semibold text-primary-foreground disabled:opacity-60"
                >
                  {savingTable ? "Saving…" : editingId ? "Save changes" : "Add table"}
                </button>
                {editingId && (
                  <button type="button" onClick={resetTableForm} className="rounded-xl border px-4 py-2.5 text-sm">
                    Cancel
                  </button>
                )}
              </div>
            </form>
            <div className="mt-7 space-y-2">
              {tables.map((table) => (
                <div key={table.id} className="flex items-center justify-between rounded-xl border border-foreground/10 bg-white/60 px-3 py-2.5">
                  <div>
                    <p className="text-sm font-medium">{table.tableNumber} · {table.capacity} seats</p>
                    <p className="text-xs text-foreground/50">{table.status}</p>
                  </div>
                  <div className="flex gap-3 text-xs font-medium">
                    <button type="button" onClick={() => startEdit(table)} className="text-sage-deep hover:underline">Edit</button>
                    <button type="button" disabled={busyId === table.id} onClick={() => void removeTable(table.id)} className="text-destructive/70 hover:text-destructive">
                      {busyId === table.id ? "Deleting…" : "Delete"}
                    </button>
                  </div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </main>
  );
}

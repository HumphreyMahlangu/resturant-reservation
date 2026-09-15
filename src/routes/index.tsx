import { createFileRoute, Link } from "@tanstack/react-router";
import { useEffect, useMemo, useState } from "react";
import {
  formatDateLong,
  formatTime,
  loadReservations,
  makeReference,
  saveReservations,
  type Reservation,
} from "../lib/reservations";
import diningRoom from "../assets/dining-room.jpg";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      { title: "Maison Verre — Restaurant Reservations" },
      {
        name: "description",
        content:
          "Book a table at Maison Verre: a seasonal tasting counter and glass-walled cellar. Choose your date, time, and party size — instant confirmation.",
      },
      { property: "og:title", content: "Maison Verre — Restaurant Reservations" },
      {
        property: "og:description",
        content:
          "Reserve your evening at Maison Verre. Seasonal tasting, candlelit cellar, instant confirmation.",
      },
      { property: "og:type", content: "website" },
      { name: "twitter:card", content: "summary_large_image" },
    ],
  }),
  component: Index,
});

const TIMES = ["17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00", "21:15"];
const SEATINGS = ["Window", "Chef's counter", "Glass cellar", "Main dining"];

const TONIGHT_SLOTS = [
  { time: "19:30", tag: "Window", detail: "Chef's counter · 4 seats left · perfect for two" },
  { time: "20:00", tag: "Cellar", detail: "Glass cellar · 6 seats left · candlelit" },
  { time: "21:15", tag: "Late", detail: "Main dining · 9 seats left · late seating" },
];

function todayISO(offset = 0) {
  const d = new Date();
  d.setDate(d.getDate() + offset);
  return d.toISOString().slice(0, 10);
}

function monthKey(iso: string) {
  return iso.slice(0, 7); // yyyy-mm
}

function monthLabel(key: string) {
  const d = new Date(`${key}-15T12:00:00`);
  return d.toLocaleDateString("en-US", { month: "long", year: "numeric" });
}

function shiftMonth(key: string, delta: number) {
  const d = new Date(`${key}-15T12:00:00`);
  d.setMonth(d.getMonth() + delta);
  return d.toISOString().slice(0, 7);
}

function daysInMonth(key: string) {
  const [y, m] = key.split("-").map(Number);
  return new Date(y!, m!, 0).getDate();
}

function firstWeekday(key: string) {
  // 0 = Monday
  const d = new Date(`${key}-01T12:00:00`);
  return (d.getDay() + 6) % 7;
}

function Index() {
  const [reservations, setReservations] = useState<Reservation[]>([]);
  useEffect(() => {
    setReservations(loadReservations().sort((a, b) => a.createdAt - b.createdAt));
  }, []);
  const [name, setName] = useState("");
  const [phone, setPhone] = useState("");
  const [date, setDate] = useState(todayISO(1));
  const [time, setTime] = useState("19:30");
  const [partySize, setPartySize] = useState(2);
  const [seating, setSeating] = useState("Window");
  const [requests, setRequests] = useState("");
  const [confirmed, setConfirmed] = useState<Reservation | null>(null);
  const [error, setError] = useState("");
  const [calMonth, setCalMonth] = useState(monthKey(todayISO()));
  const [calDay, setCalDay] = useState(todayISO());

  const upcoming = useMemo(
    () => reservations.filter((r) => r.date >= todayISO()),
    [reservations],
  );

  const bookedByDate = useMemo(() => {
    const map = new Map<string, Reservation[]>();
    for (const r of reservations) {
      const list = map.get(r.date) ?? [];
      list.push(r);
      map.set(r.date, list);
    }
    return map;
  }, [reservations]);

  const selectedDayBookings = useMemo(
    () =>
      (bookedByDate.get(calDay) ?? [])
        .slice()
        .sort((a, b) => a.time.localeCompare(b.time)),
    [bookedByDate, calDay],
  );

  function confirmReservation(e: React.FormEvent) {
    e.preventDefault();
    if (!name.trim() || !phone.trim()) {
      setError("Please add your name and phone number.");
      return;
    }
    setError("");
    const reservation: Reservation = {
      id: crypto.randomUUID(),
      reference: makeReference(),
      name: name.trim(),
      phone: phone.trim(),
      date,
      time,
      partySize,
      seating,
      requests: requests.trim() || undefined,
      createdAt: Date.now(),
    };
    const next = [...reservations, reservation];
    saveReservations(next);
    setReservations(next);
    setConfirmed(reservation);
    setRequests("");
    document.getElementById("confirmation")?.scrollIntoView({ behavior: "smooth" });
  }

  function cancelReservation(id: string) {
    const next = reservations.filter((r) => r.id !== id);
    saveReservations(next);
    setReservations(next);
    setConfirmed((c) => (c?.id === id ? null : c));
  }

  function pickSlot(slotTime: string, slotSeating: string) {
    setTime(slotTime);
    setDate(todayISO());
    setSeating(slotSeating === "Cellar" ? "Glass cellar" : slotSeating);
    document.getElementById("book")?.scrollIntoView({ behavior: "smooth" });
  }

  const inputCls =
    "mt-1.5 w-full rounded-xl border border-white/80 bg-white/70 px-3.5 py-2.5 text-sm font-medium text-foreground outline-none focus:border-sage/50";

  return (
    <div className="relative min-h-screen overflow-hidden bg-background font-sans text-foreground">
      <div className="pointer-events-none absolute -top-24 -left-16 size-[440px] rounded-full bg-sage/30 blur-3xl" />
      <div className="pointer-events-none absolute top-40 -right-24 size-[400px] rounded-full bg-terra/25 blur-3xl" />
      <div className="pointer-events-none absolute bottom-0 left-1/3 size-[380px] rounded-full bg-white/60 blur-3xl" />

      <header className="relative z-10 mx-auto max-w-6xl px-6 pt-6">
        <nav className="flex items-center justify-between rounded-2xl border border-white/70 bg-white/55 px-6 py-4 shadow-[0_8px_30px_rgba(43,39,33,0.06)] backdrop-blur-xl">
          <Link to="/" className="flex items-center gap-2.5">
            <div className="grid size-9 place-items-center rounded-xl bg-sage-deep font-display text-lg text-primary-foreground">
              M
            </div>
            <div>
              <p className="font-display text-lg leading-none">Maison Verre</p>
              <p className="text-[11px] uppercase tracking-[0.2em] text-foreground/40">
                Reservations
              </p>
            </div>
          </Link>
          <div className="hidden items-center gap-8 text-sm font-medium text-foreground/70 md:flex">
            <a href="#book" className="hover:text-foreground">Book</a>
            <a href="#tonight" className="hover:text-foreground">Tonight</a>
            <a href="#calendar" className="hover:text-foreground">Calendar</a>
            <a href="#your-tables" className="hover:text-foreground">Your tables</a>
            <a href="#experience" className="hover:text-foreground">The experience</a>
          </div>
          <a
            href="#book"
            className="rounded-full bg-primary px-5 py-2.5 text-sm font-medium text-primary-foreground transition hover:bg-primary/90"
          >
            Book a Table
          </a>
        </nav>
      </header>

      <main className="relative z-10 mx-auto max-w-6xl px-6 py-12">
        <div className="grid gap-8 lg:grid-cols-[1.1fr_0.9fr] lg:items-center">
          <div>
            <span className="inline-flex items-center gap-2 rounded-full border border-sage/30 bg-white/60 px-3 py-1.5 text-xs font-medium text-sage-deep backdrop-blur-md">
              <span className="size-1.5 rounded-full bg-sage" /> Now seating · 214 King Street
            </span>
            <h1 className="mt-6 font-display text-5xl leading-[1.05] tracking-tight md:text-6xl">
              Reserve your evening at <span className="text-sage-deep">Maison Verre</span>
            </h1>
            <p className="mt-5 max-w-md text-base leading-relaxed text-foreground/60">
              A seasonal tasting counter and glass-walled cellar. Pick a date, choose your party,
              and we'll hold the light by the window.
            </p>
            <div className="mt-8 flex flex-wrap items-center gap-6">
              <div>
                <p className="font-display text-2xl">4.9</p>
                <p className="text-xs text-foreground/50">2,340 reviews</p>
              </div>
              <div className="h-10 w-px bg-foreground/10" />
              <div>
                <p className="font-display text-2xl">Est. 2016</p>
                <p className="text-xs text-foreground/50">Two Michelin stars</p>
              </div>
              <div className="h-10 w-px bg-foreground/10" />
              <div>
                <p className="font-display text-2xl">120</p>
                <p className="text-xs text-foreground/50">Seats nightly</p>
              </div>
            </div>
          </div>

          <section
            id="book"
            className="scroll-mt-8 rounded-3xl border border-white/70 bg-white/60 p-6 shadow-[0_20px_60px_rgba(43,39,33,0.10)] backdrop-blur-2xl"
          >
            <div className="flex items-center justify-between">
              <h2 className="font-display text-xl">Book a table</h2>
              <span className="rounded-full bg-sage/15 px-3 py-1 text-xs font-medium text-sage-deep">
                Instant confirm
              </span>
            </div>

            <form onSubmit={confirmReservation}>
              <div className="mt-5 grid grid-cols-2 gap-3">
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Date</span>
                  <input
                    type="date"
                    required
                    min={todayISO()}
                    value={date}
                    onChange={(e) => setDate(e.target.value)}
                    className={inputCls}
                  />
                </label>
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Time</span>
                  <select value={time} onChange={(e) => setTime(e.target.value)} className={inputCls}>
                    {TIMES.map((t) => (
                      <option key={t} value={t}>
                        {formatTime(t)}
                      </option>
                    ))}
                  </select>
                </label>
              </div>

              <div className="mt-3 grid grid-cols-2 gap-3">
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Party size</span>
                  <select
                    value={partySize}
                    onChange={(e) => setPartySize(Number(e.target.value))}
                    className={inputCls}
                  >
                    {[1, 2, 3, 4, 5, 6, 7, 8].map((n) => (
                      <option key={n} value={n}>
                        {n} {n === 1 ? "guest" : "guests"}
                      </option>
                    ))}
                  </select>
                </label>
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Seating</span>
                  <select
                    value={seating}
                    onChange={(e) => setSeating(e.target.value)}
                    className={inputCls}
                  >
                    {SEATINGS.map((s) => (
                      <option key={s}>{s}</option>
                    ))}
                  </select>
                </label>
              </div>

              <div className="mt-3 grid grid-cols-2 gap-3">
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Name</span>
                  <input
                    type="text"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    placeholder="Elena Marchetti"
                    className={inputCls}
                  />
                </label>
                <label className="block">
                  <span className="text-xs font-medium text-foreground/50">Phone</span>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="+1 (415) 555-0182"
                    className={inputCls}
                  />
                </label>
              </div>

              <label className="mt-3 block">
                <span className="text-xs font-medium text-foreground/50">Special requests</span>
                <input
                  type="text"
                  value={requests}
                  onChange={(e) => setRequests(e.target.value)}
                  placeholder="Allergies, celebrations, accessibility…"
                  className={inputCls}
                />
              </label>

              {error && <p className="mt-3 text-xs font-medium text-destructive">{error}</p>}

              <button
                type="submit"
                className="mt-5 w-full rounded-xl bg-primary py-3 text-sm font-semibold text-primary-foreground transition hover:bg-primary/90"
              >
                Confirm reservation
              </button>
              <p className="mt-3 text-center text-xs text-foreground/40">
                Free cancellation up to 4 hours before
              </p>
            </form>
          </section>
        </div>

        {confirmed && (
          <section
            id="confirmation"
            className="mt-10 scroll-mt-8 rounded-3xl border border-sage/30 bg-white/70 p-6 backdrop-blur-2xl sm:p-8"
          >
            <div className="flex flex-col gap-6 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <p className="text-xs font-medium uppercase tracking-[0.2em] text-sage-deep">
                  Booking confirmed
                </p>
                <h3 className="mt-2 font-display text-2xl sm:text-3xl">
                  See you {formatDateLong(confirmed.date)} at {formatTime(confirmed.time)},{" "}
                  {confirmed.name.split(" ")[0]}.
                </h3>
                <p className="mt-2 text-sm text-foreground/60">
                  {confirmed.partySize} {confirmed.partySize === 1 ? "guest" : "guests"} ·{" "}
                  {confirmed.seating} — show this reference at the door.
                </p>
              </div>
              <div className="shrink-0 rounded-2xl bg-primary px-6 py-4 text-center text-primary-foreground">
                <p className="text-xs uppercase tracking-[0.2em] opacity-70">Reference</p>
                <p className="mt-1 font-display text-2xl">{confirmed.reference}</p>
              </div>
            </div>
          </section>
        )}

        <div id="tonight" className="mt-14 scroll-mt-8">
          <div className="flex items-end justify-between">
            <h3 className="font-display text-2xl">Available tonight</h3>
            <span className="text-sm text-foreground/50">Live availability</span>
          </div>
          <div className="mt-5 grid gap-4 sm:grid-cols-3">
            {TONIGHT_SLOTS.map((slot) => (
              <div
                key={slot.time}
                className="rounded-2xl border border-white/70 bg-white/55 p-5 backdrop-blur-xl transition hover:bg-white/75"
              >
                <div className="flex items-center justify-between">
                  <span className="font-display text-3xl">{formatTime(slot.time)}</span>
                  <span
                    className={`rounded-full px-2.5 py-1 text-[11px] font-semibold ${
                      slot.tag === "Window"
                        ? "bg-sage/20 text-sage-deep"
                        : slot.tag === "Cellar"
                          ? "bg-terra/20 text-terra"
                          : "bg-foreground/10 text-foreground/60"
                    }`}
                  >
                    {slot.tag}
                  </span>
                </div>
                <p className="mt-3 text-sm text-foreground/60">{slot.detail}</p>
                <button
                  onClick={() => pickSlot(slot.time, slot.tag)}
                  className="mt-4 w-full rounded-lg py-2 text-sm font-medium text-sage-deep transition hover:bg-sage/10"
                >
                  Select {formatTime(slot.time)}
                </button>
              </div>
            ))}
          </div>
        </div>

        <div id="calendar" className="mt-14 scroll-mt-8">
          <div className="flex items-end justify-between">
            <h3 className="font-display text-2xl">Booking calendar</h3>
            <span className="text-sm text-foreground/50">See which times are taken</span>
          </div>
          <div className="mt-5 grid gap-4 lg:grid-cols-[1.1fr_0.9fr]">
            <div className="rounded-2xl border border-white/70 bg-white/55 p-5 backdrop-blur-xl">
              <div className="flex items-center justify-between">
                <button
                  onClick={() => setCalMonth(shiftMonth(calMonth, -1))}
                  className="grid size-8 place-items-center rounded-lg text-foreground/60 transition hover:bg-sage/10 hover:text-sage-deep"
                  aria-label="Previous month"
                >
                  ←
                </button>
                <p className="font-display text-lg">{monthLabel(calMonth)}</p>
                <button
                  onClick={() => setCalMonth(shiftMonth(calMonth, 1))}
                  className="grid size-8 place-items-center rounded-lg text-foreground/60 transition hover:bg-sage/10 hover:text-sage-deep"
                  aria-label="Next month"
                >
                  →
                </button>
              </div>
              <div className="mt-4 grid grid-cols-7 gap-1 text-center text-[11px] font-semibold uppercase tracking-wide text-foreground/40">
                {["Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"].map((d) => (
                  <span key={d}>{d}</span>
                ))}
              </div>
              <div className="mt-1 grid grid-cols-7 gap-1">
                {Array.from({ length: firstWeekday(calMonth) }).map((_, i) => (
                  <span key={`blank-${i}`} />
                ))}
                {Array.from({ length: daysInMonth(calMonth) }).map((_, i) => {
                  const iso = `${calMonth}-${String(i + 1).padStart(2, "0")}`;
                  const count = bookedByDate.get(iso)?.length ?? 0;
                  const isSelected = iso === calDay;
                  const isToday = iso === todayISO();
                  return (
                    <button
                      key={iso}
                      onClick={() => setCalDay(iso)}
                      className={`relative flex flex-col items-center rounded-xl py-2 text-sm transition ${
                        isSelected
                          ? "bg-sage-deep text-primary-foreground"
                          : isToday
                            ? "bg-sage/15 font-semibold text-sage-deep"
                            : "hover:bg-white/70"
                      }`}
                    >
                      {i + 1}
                      {count > 0 && (
                        <span
                          className={`mt-0.5 flex h-1 gap-0.5 ${
                            isSelected ? "opacity-90" : ""
                          }`}
                        >
                          {Array.from({ length: Math.min(count, 3) }).map((_, d) => (
                            <span
                              key={d}
                              className={`size-1 rounded-full ${
                                isSelected ? "bg-primary-foreground" : "bg-terra"
                              }`}
                            />
                          ))}
                        </span>
                      )}
                    </button>
                  );
                })}
              </div>
            </div>

            <div className="rounded-2xl border border-white/70 bg-white/55 p-5 backdrop-blur-xl">
              <div className="flex items-center justify-between">
                <p className="font-display text-lg">{formatDateLong(calDay)}</p>
                <span className="rounded-full bg-sage/15 px-3 py-1 text-xs font-medium text-sage-deep">
                  {selectedDayBookings.length}{" "}
                  {selectedDayBookings.length === 1 ? "booking" : "bookings"}
                </span>
              </div>
              <div className="mt-4 space-y-1.5">
                {TIMES.map((t) => {
                  const bookings = selectedDayBookings.filter((r) => r.time === t);
                  const taken = bookings.length > 0;
                  return (
                    <div
                      key={t}
                      className={`flex items-center justify-between rounded-xl px-3.5 py-2.5 text-sm ${
                        taken
                          ? "bg-terra/15"
                          : "bg-white/50"
                      }`}
                    >
                      <span className="font-medium">{formatTime(t)}</span>
                      {taken ? (
                        <span className="text-xs text-terra">
                          Taken ·{" "}
                          {bookings
                            .map((b) => `${b.name.split(" ")[0]} (${b.partySize})`)
                            .join(", ")}
                        </span>
                      ) : (
                        <span className="text-xs font-medium text-sage-deep">Open</span>
                      )}
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>

        <div id="your-tables" className="mt-14 scroll-mt-8">
          <div className="flex items-end justify-between">
            <h3 className="font-display text-2xl">Your tables</h3>
            <span className="text-sm text-foreground/50">
              {upcoming.length} {upcoming.length === 1 ? "reservation" : "reservations"}
            </span>
          </div>
          {upcoming.length === 0 ? (
            <div className="mt-5 rounded-2xl border border-white/70 bg-white/55 p-8 text-center backdrop-blur-xl">
              <p className="text-sm text-foreground/50">
                No upcoming reservations yet — book your first table above.
              </p>
            </div>
          ) : (
            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              {upcoming.map((r) => (
                <div
                  key={r.id}
                  className="rounded-2xl border border-white/70 bg-white/55 p-5 backdrop-blur-xl"
                >
                  <div className="flex items-center justify-between">
                    <p className="font-display text-lg">
                      {formatDateLong(r.date)} · {formatTime(r.time)}
                    </p>
                    <span className="rounded-full bg-sage/20 px-2.5 py-1 text-[11px] font-semibold text-sage-deep">
                      Confirmed
                    </span>
                  </div>
                  <p className="mt-2 text-sm text-foreground/60">
                    {r.partySize} {r.partySize === 1 ? "guest" : "guests"} · {r.seating} · {r.name}
                  </p>
                  {r.requests && (
                    <p className="mt-1 text-xs text-foreground/40">“{r.requests}”</p>
                  )}
                  <div className="mt-4 flex items-center justify-between">
                    <span className="text-xs font-medium text-foreground/40">
                      Ref · {r.reference}
                    </span>
                    <button
                      onClick={() => cancelReservation(r.id)}
                      className="text-xs font-medium text-destructive/70 transition hover:text-destructive"
                    >
                      Cancel
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        <div
          id="experience"
          className="mt-14 scroll-mt-8 overflow-hidden rounded-3xl border border-white/70 bg-white/40 backdrop-blur-2xl"
        >
          <div className="grid md:grid-cols-2">
            <img
              src={diningRoom}
              alt="Candlelit glass dining room at Maison Verre"
              loading="lazy"
              width={1024}
              height={1024}
              className="h-full min-h-[280px] w-full object-cover"
            />
            <div className="p-8">
              <p className="text-xs font-medium uppercase tracking-[0.2em] text-terra">
                The experience
              </p>
              <p className="mt-4 font-display text-2xl leading-snug">
                A daily-changing counter, a living wine wall, and light that shifts with the
                evening.
              </p>
              <div className="mt-6 space-y-3">
                {[
                  "Welcome & aperitif on the terrace",
                  "Seven-course seasonal tasting",
                  "Cellar pairing & final pour",
                ].map((step, i) => (
                  <div key={step} className="flex items-center gap-3 text-sm text-foreground/70">
                    <span className="grid size-8 place-items-center rounded-lg bg-white/70 text-sage-deep">
                      0{i + 1}
                    </span>
                    {step}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </main>

      <footer className="relative z-10 mx-auto max-w-6xl px-6 pb-10">
        <div className="flex flex-col items-center justify-between gap-2 border-t border-foreground/10 pt-6 text-xs text-foreground/40 sm:flex-row">
          <span>Maison Verre · 214 King Street</span>
          <span>Tue–Sun · 5:30 PM – 11:00 PM</span>
        </div>
      </footer>
    </div>
  );
}

package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 7)
    private String reference;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 40)
    private String phone;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private int partySize;

    @Column(nullable = false, length = 40)
    private String seating;

    @Column(length = 500)
    private String requests;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;

    protected Reservation() {
    }

    public Reservation(String reference, String name, String phone, LocalDate date, LocalTime time,
                       int partySize, String seating, String requests) {
        this.reference = reference;
        this.name = name;
        this.phone = phone;
        this.date = date;
        this.time = time;
        this.partySize = partySize;
        this.seating = seating;
        this.requests = requests;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getReference() { return reference; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public LocalDate getDate() { return date; }
    public LocalTime getTime() { return time; }
    public int getPartySize() { return partySize; }
    public String getSeating() { return seating; }
    public String getRequests() { return requests; }
    public Instant getCreatedAt() { return createdAt; }
}

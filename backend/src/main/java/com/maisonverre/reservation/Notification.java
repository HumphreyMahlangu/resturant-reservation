package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
    @Column(nullable = false, length = 2000)
    private String message;
    @Column(nullable = false)
    private boolean read = false;
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    protected Notification() {}
    public Notification(UserAccount user, String message, boolean read) {
        this.user = user; this.message = message; this.read = read;
    }
    public UUID getId() { return id; }
    public UserAccount getUser() { return user; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public Instant getCreatedAt() { return createdAt; }
    public void update(UserAccount user, String message, boolean read) {
        this.user = user; this.message = message; this.read = read;
    }
}

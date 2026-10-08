package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
    @Column(nullable = false, length = 120)
    private String name;
    @Column(nullable = false, unique = true, length = 120)
    private String email;
    @Column(length = 40)
    private String phone;
    @Column(name = "password_hash", length = 255)
    private String passwordHash;
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";
    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
    protected UserAccount() {}
    public UserAccount(String name, String email, String phone, Role role) {
        this.name = name; this.email = email; this.phone = phone; this.role = role;
    }
    public UUID getId() { return id; }
    public Role getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getStatus() { return status; }
    public String getPasswordHash() { return passwordHash; }
    public Instant getCreatedAt() { return createdAt; }
    public void update(String name, String email, String phone, String passwordHash, Role role, String status) {
        this.name = name; this.email = email; this.phone = phone; this.passwordHash = passwordHash;
        this.role = role; this.status = status;
    }
}

package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 40)
    private String name;
    @Column(length = 255)
    private String description;
    protected Role() {}
    public Role(String name, String description) { this.name = name; this.description = description; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void update(String name, String description) { this.name = name; this.description = description; }
}

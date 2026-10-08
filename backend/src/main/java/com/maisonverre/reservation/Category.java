package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(length = 255)
    private String description;
    protected Category() {}
    public Category(String name, String description) { this.name = name; this.description = description; }
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public void update(String name, String description) { this.name = name; this.description = description; }
}

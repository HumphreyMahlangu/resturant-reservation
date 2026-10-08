package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @Column(nullable = false, length = 150)
    private String name;
    @Column(length = 1000)
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private int quantityAvailable;
    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";
    protected MenuItem() {}
    public MenuItem(Category category, String name, String description, BigDecimal price, int quantityAvailable) {
        this.category = category; this.name = name; this.description = description;
        this.price = price; this.quantityAvailable = quantityAvailable;
    }
    public UUID getId() { return id; }
    public Category getCategory() { return category; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getQuantityAvailable() { return quantityAvailable; }
    public String getStatus() { return status; }
    public void update(Category category, String name, String description, BigDecimal price, int quantityAvailable, String status) {
        this.category = category; this.name = name; this.description = description; this.price = price;
        this.quantityAvailable = quantityAvailable; this.status = status;
    }
}

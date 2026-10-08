package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "restaurant_tables")
public class RestaurantTable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true, length = 30)
    private String tableNumber;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE";
    protected RestaurantTable() {}
    public RestaurantTable(String tableNumber, int capacity) {
        this.tableNumber = tableNumber; this.capacity = capacity;
    }
    public UUID getId() { return id; }
    public String getTableNumber() { return tableNumber; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }
    public void update(String tableNumber, int capacity, String status) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }
}

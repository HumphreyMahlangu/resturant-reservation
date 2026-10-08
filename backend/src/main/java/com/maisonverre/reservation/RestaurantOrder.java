package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "restaurant_orders")
public class RestaurantOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id")
    private RestaurantTable table;
    @Column(nullable = false, updatable = false)
    private Instant orderDate = Instant.now();
    @Column(nullable = false, length = 20)
    private String orderStatus = "PENDING";
    @Column(nullable = false, length = 20)
    private String paymentStatus = "UNPAID";
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    protected RestaurantOrder() {}
    public RestaurantOrder(UserAccount user, RestaurantTable table, BigDecimal totalAmount) {
        this.user = user; this.table = table; this.totalAmount = totalAmount;
    }
    public UUID getId() { return id; }
    public UserAccount getUser() { return user; }
    public RestaurantTable getTable() { return table; }
    public Instant getOrderDate() { return orderDate; }
    public String getOrderStatus() { return orderStatus; }
    public String getPaymentStatus() { return paymentStatus; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void update(UserAccount user, RestaurantTable table, String orderStatus, String paymentStatus, BigDecimal totalAmount) {
        this.user = user; this.table = table; this.orderStatus = orderStatus; this.paymentStatus = paymentStatus; this.totalAmount = totalAmount;
    }
}

package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private RestaurantOrder order;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false, length = 30)
    private String paymentMethod;
    @Column(nullable = false, length = 20)
    private String paymentStatus = "PENDING";
    @Column(nullable = false, updatable = false)
    private Instant paymentDate = Instant.now();
    @Column(length = 100)
    private String transactionReference;
    protected Payment() {}
    public Payment(RestaurantOrder order, BigDecimal amount, String paymentMethod, String transactionReference) {
        this.order = order; this.amount = amount; this.paymentMethod = paymentMethod; this.transactionReference = transactionReference;
    }
    public UUID getId() { return id; }
    public RestaurantOrder getOrder() { return order; }
    public BigDecimal getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getPaymentStatus() { return paymentStatus; }
    public Instant getPaymentDate() { return paymentDate; }
    public String getTransactionReference() { return transactionReference; }
    public void update(RestaurantOrder order, BigDecimal amount, String paymentMethod, String paymentStatus, String transactionReference) {
        this.order = order; this.amount = amount; this.paymentMethod = paymentMethod; this.paymentStatus = paymentStatus; this.transactionReference = transactionReference;
    }
}

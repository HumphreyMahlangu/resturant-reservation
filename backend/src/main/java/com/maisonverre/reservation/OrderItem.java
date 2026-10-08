package com.maisonverre.reservation;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private RestaurantOrder order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    @Column(nullable = false)
    private int quantity;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    protected OrderItem() {}
    public OrderItem(RestaurantOrder order, MenuItem menuItem, int quantity, BigDecimal unitPrice) {
        this.order = order; this.menuItem = menuItem; this.quantity = quantity; this.unitPrice = unitPrice;
    }
    public UUID getId() { return id; }
    public RestaurantOrder getOrder() { return order; }
    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void update(RestaurantOrder order, MenuItem menuItem, int quantity, BigDecimal unitPrice) {
        this.order = order; this.menuItem = menuItem; this.quantity = quantity; this.unitPrice = unitPrice;
    }
}

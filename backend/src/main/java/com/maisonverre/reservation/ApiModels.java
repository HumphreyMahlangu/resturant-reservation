package com.maisonverre.reservation;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class ApiModels {
    private ApiModels() {}
    public record RoleRequest(@NotBlank @Size(max=40) String name, @Size(max=255) String description) {}
    public record RoleResponse(UUID id, String name, String description) {
        static RoleResponse from(Role r) { return new RoleResponse(r.getId(), r.getName(), r.getDescription()); }
    }
    public record UserRequest(@NotBlank @Size(max=120) String name, @NotBlank @Email @Size(max=120) String email,
                              @Size(max=40) String phone, String passwordHash, UUID roleId, @Size(max=20) String status) {}
    public record UserResponse(UUID id, UUID roleId, String name, String email, String phone, String status, Instant createdAt) {
        static UserResponse from(UserAccount u) { return new UserResponse(u.getId(), u.getRole() == null ? null : u.getRole().getId(), u.getName(), u.getEmail(), u.getPhone(), u.getStatus(), u.getCreatedAt()); }
    }
    public record CategoryRequest(@NotBlank @Size(max=100) String name, @Size(max=255) String description) {}
    public record CategoryResponse(UUID id, String name, String description) {
        static CategoryResponse from(Category c) { return new CategoryResponse(c.getId(), c.getName(), c.getDescription()); }
    }
    public record MenuItemRequest(@NotNull UUID categoryId, @NotBlank @Size(max=150) String name, @Size(max=1000) String description,
                                  @NotNull @DecimalMin("0.00") BigDecimal price, @Min(0) int quantityAvailable, @Size(max=20) String status) {}
    public record MenuItemResponse(UUID id, UUID categoryId, String name, String description, BigDecimal price, int quantityAvailable, String status) {
        static MenuItemResponse from(MenuItem m) { return new MenuItemResponse(m.getId(), m.getCategory().getId(), m.getName(), m.getDescription(), m.getPrice(), m.getQuantityAvailable(), m.getStatus()); }
    }
    public record OrderRequest(@NotNull UUID userId, UUID tableId, @Size(max=20) String orderStatus, @Size(max=20) String paymentStatus,
                               @NotNull @DecimalMin("0.00") BigDecimal totalAmount) {}
    public record OrderResponse(UUID id, UUID userId, UUID tableId, Instant orderDate, String orderStatus, String paymentStatus, BigDecimal totalAmount) {
        static OrderResponse from(RestaurantOrder o) { return new OrderResponse(o.getId(), o.getUser().getId(), o.getTable() == null ? null : o.getTable().getId(), o.getOrderDate(), o.getOrderStatus(), o.getPaymentStatus(), o.getTotalAmount()); }
    }
    public record OrderItemRequest(@NotNull UUID orderId, @NotNull UUID menuItemId, @Min(1) int quantity,
                                   @NotNull @DecimalMin("0.00") BigDecimal unitPrice) {}
    public record OrderItemResponse(UUID id, UUID orderId, UUID menuItemId, int quantity, BigDecimal unitPrice) {
        static OrderItemResponse from(OrderItem i) { return new OrderItemResponse(i.getId(), i.getOrder().getId(), i.getMenuItem().getId(), i.getQuantity(), i.getUnitPrice()); }
    }
    public record PaymentRequest(@NotNull UUID orderId, @NotNull @DecimalMin("0.00") BigDecimal amount, @NotBlank @Size(max=30) String paymentMethod,
                                 @Size(max=20) String paymentStatus, @Size(max=100) String transactionReference) {}
    public record PaymentResponse(UUID id, UUID orderId, BigDecimal amount, String paymentMethod, String paymentStatus, Instant paymentDate, String transactionReference) {
        static PaymentResponse from(Payment p) { return new PaymentResponse(p.getId(), p.getOrder().getId(), p.getAmount(), p.getPaymentMethod(), p.getPaymentStatus(), p.getPaymentDate(), p.getTransactionReference()); }
    }
    public record ReviewRequest(@NotNull UUID userId, @NotNull UUID menuItemId, @Min(1) @Max(5) int rating, @Size(max=2000) String comment) {}
    public record ReviewResponse(UUID id, UUID userId, UUID menuItemId, int rating, String comment, Instant reviewDate) {
        static ReviewResponse from(Review r) { return new ReviewResponse(r.getId(), r.getUser().getId(), r.getMenuItem().getId(), r.getRating(), r.getComment(), r.getReviewDate()); }
    }
    public record NotificationRequest(@NotNull UUID userId, @NotBlank @Size(max=2000) String message, boolean read) {}
    public record NotificationResponse(UUID id, UUID userId, String message, boolean read, Instant createdAt) {
        static NotificationResponse from(Notification n) { return new NotificationResponse(n.getId(), n.getUser().getId(), n.getMessage(), n.isRead(), n.getCreatedAt()); }
    }
}

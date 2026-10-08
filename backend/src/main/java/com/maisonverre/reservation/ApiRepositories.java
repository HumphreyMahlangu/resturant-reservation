package com.maisonverre.reservation;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface RoleRepository extends JpaRepository<Role, UUID> { boolean existsByName(String name); boolean existsByNameAndIdNot(String name, UUID id); java.util.Optional<Role> findByName(String name); }
interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, UUID id);
    java.util.Optional<UserAccount> findByEmail(String email);
    @Query("select u from UserAccount u join fetch u.role where u.email = :email")
    java.util.Optional<UserAccount> findByEmailWithRole(String email);
}
interface CategoryRepository extends JpaRepository<Category, UUID> { boolean existsByName(String name); boolean existsByNameAndIdNot(String name, UUID id); }
interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {}
interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, UUID> {}
interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {}
interface PaymentRepository extends JpaRepository<Payment, UUID> { boolean existsByOrderId(UUID orderId); }
interface ReviewRepository extends JpaRepository<Review, UUID> {}
interface NotificationRepository extends JpaRepository<Notification, UUID> {}

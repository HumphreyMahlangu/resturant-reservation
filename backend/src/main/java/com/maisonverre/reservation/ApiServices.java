package com.maisonverre.reservation;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

final class ApiSupport {
    private ApiSupport() {}
    static <T> T required(Optional<T> value, String message) {
        return value.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, message));
    }
    static String text(String value, String fallback) { return value == null || value.isBlank() ? fallback : value.trim().toUpperCase(); }
}

@Service class RoleApiService {
    private final RoleRepository repo;
    RoleApiService(RoleRepository repo) { this.repo = repo; }
    List<ApiModels.RoleResponse> all() { return repo.findAll().stream().map(ApiModels.RoleResponse::from).toList(); }
    ApiModels.RoleResponse create(ApiModels.RoleRequest r) { String n=r.name().trim(); if(repo.existsByName(n)) throw conflict("Role name already exists"); return ApiModels.RoleResponse.from(repo.save(new Role(n,r.description()))); }
    ApiModels.RoleResponse update(UUID id, ApiModels.RoleRequest r) { Role e=ApiSupport.required(repo.findById(id),"Role not found"); String n=r.name().trim(); if(repo.existsByNameAndIdNot(n,id)) throw conflict("Role name already exists"); e.update(n,r.description()); return ApiModels.RoleResponse.from(repo.save(e)); }
    void delete(UUID id) { if(!repo.existsById(id)) throw notFound("Role not found"); repo.deleteById(id); }
    private static ResponseStatusException conflict(String m){return new ResponseStatusException(HttpStatus.CONFLICT,m);} private static ResponseStatusException notFound(String m){return new ResponseStatusException(HttpStatus.NOT_FOUND,m);}
}

@Service class UserApiService {
    private final UserAccountRepository repo; private final RoleRepository roles;
    UserApiService(UserAccountRepository repo, RoleRepository roles) { this.repo=repo; this.roles=roles; }
    List<ApiModels.UserResponse> all(){return repo.findAll().stream().map(ApiModels.UserResponse::from).toList();}
    ApiModels.UserResponse create(ApiModels.UserRequest r){String email=r.email().trim().toLowerCase(); if(repo.existsByEmail(email)) throw conflict("Email already exists"); UserAccount e=new UserAccount(r.name().trim(),email,r.phone(),role(r.roleId())); e.update(r.name().trim(),email,r.phone(),r.passwordHash(),role(r.roleId()),ApiSupport.text(r.status(),"ACTIVE")); return ApiModels.UserResponse.from(repo.save(e));}
    ApiModels.UserResponse update(UUID id,ApiModels.UserRequest r){UserAccount e=ApiSupport.required(repo.findById(id),"User not found"); String email=r.email().trim().toLowerCase(); if(repo.existsByEmailAndIdNot(email,id)) throw conflict("Email already exists"); e.update(r.name().trim(),email,r.phone(),r.passwordHash(),role(r.roleId()),ApiSupport.text(r.status(),"ACTIVE")); return ApiModels.UserResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id)) throw notFound("User not found"); repo.deleteById(id);}
    private Role role(UUID id){return id==null?null:ApiSupport.required(roles.findById(id),"Role not found");}
    private static ResponseStatusException conflict(String m){return new ResponseStatusException(HttpStatus.CONFLICT,m);} private static ResponseStatusException notFound(String m){return new ResponseStatusException(HttpStatus.NOT_FOUND,m);}
}

@Service class CategoryApiService {
    private final CategoryRepository repo; CategoryApiService(CategoryRepository repo){this.repo=repo;}
    List<ApiModels.CategoryResponse> all(){return repo.findAll().stream().map(ApiModels.CategoryResponse::from).toList();}
    ApiModels.CategoryResponse create(ApiModels.CategoryRequest r){String n=r.name().trim();if(repo.existsByName(n))throw conflict();return ApiModels.CategoryResponse.from(repo.save(new Category(n,r.description())));}
    ApiModels.CategoryResponse update(UUID id,ApiModels.CategoryRequest r){Category e=ApiSupport.required(repo.findById(id),"Category not found");String n=r.name().trim();if(repo.existsByNameAndIdNot(n,id))throw conflict();e.update(n,r.description());return ApiModels.CategoryResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");repo.deleteById(id);} private static ResponseStatusException conflict(){return new ResponseStatusException(HttpStatus.CONFLICT,"Category name already exists");}
}

@Service class MenuItemApiService {
    private final MenuItemRepository repo; private final CategoryRepository categories; MenuItemApiService(MenuItemRepository repo,CategoryRepository categories){this.repo=repo;this.categories=categories;}
    List<ApiModels.MenuItemResponse> all(){return repo.findAll().stream().map(ApiModels.MenuItemResponse::from).toList();}
    ApiModels.MenuItemResponse create(ApiModels.MenuItemRequest r){MenuItem e=new MenuItem(category(r.categoryId()),r.name().trim(),r.description(),r.price(),r.quantityAvailable());e.update(category(r.categoryId()),r.name().trim(),r.description(),r.price(),r.quantityAvailable(),ApiSupport.text(r.status(),"AVAILABLE"));return ApiModels.MenuItemResponse.from(repo.save(e));}
    ApiModels.MenuItemResponse update(UUID id,ApiModels.MenuItemRequest r){MenuItem e=ApiSupport.required(repo.findById(id),"Menu item not found");e.update(category(r.categoryId()),r.name().trim(),r.description(),r.price(),r.quantityAvailable(),ApiSupport.text(r.status(),"AVAILABLE"));return ApiModels.MenuItemResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Menu item not found");repo.deleteById(id);} private Category category(UUID id){return ApiSupport.required(categories.findById(id),"Category not found");}
}

@Service class OrderApiService {
    private final RestaurantOrderRepository repo; private final UserAccountRepository users; private final RestaurantTableRepository tables;
    OrderApiService(RestaurantOrderRepository repo,UserAccountRepository users,RestaurantTableRepository tables){this.repo=repo;this.users=users;this.tables=tables;}
    List<ApiModels.OrderResponse> all(){return repo.findAll().stream().map(ApiModels.OrderResponse::from).toList();}
    ApiModels.OrderResponse create(ApiModels.OrderRequest r){RestaurantOrder e=new RestaurantOrder(user(r.userId()),table(r.tableId()),r.totalAmount()); e.update(user(r.userId()),table(r.tableId()),ApiSupport.text(r.orderStatus(),"PENDING"),ApiSupport.text(r.paymentStatus(),"UNPAID"),r.totalAmount());return ApiModels.OrderResponse.from(repo.save(e));}
    ApiModels.OrderResponse update(UUID id,ApiModels.OrderRequest r){RestaurantOrder e=ApiSupport.required(repo.findById(id),"Order not found");e.update(user(r.userId()),table(r.tableId()),ApiSupport.text(r.orderStatus(),"PENDING"),ApiSupport.text(r.paymentStatus(),"UNPAID"),r.totalAmount());return ApiModels.OrderResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order not found");repo.deleteById(id);} private UserAccount user(UUID id){return ApiSupport.required(users.findById(id),"User not found");} private RestaurantTable table(UUID id){return id==null?null:ApiSupport.required(tables.findById(id),"Table not found");}
}

@Service class OrderItemApiService {
    private final OrderItemRepository repo; private final RestaurantOrderRepository orders; private final MenuItemRepository items;
    OrderItemApiService(OrderItemRepository repo,RestaurantOrderRepository orders,MenuItemRepository items){this.repo=repo;this.orders=orders;this.items=items;}
    List<ApiModels.OrderItemResponse> all(){return repo.findAll().stream().map(ApiModels.OrderItemResponse::from).toList();}
    ApiModels.OrderItemResponse create(ApiModels.OrderItemRequest r){return ApiModels.OrderItemResponse.from(repo.save(new OrderItem(order(r.orderId()),item(r.menuItemId()),r.quantity(),r.unitPrice())));}
    ApiModels.OrderItemResponse update(UUID id,ApiModels.OrderItemRequest r){OrderItem e=ApiSupport.required(repo.findById(id),"Order item not found");e.update(order(r.orderId()),item(r.menuItemId()),r.quantity(),r.unitPrice());return ApiModels.OrderItemResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Order item not found");repo.deleteById(id);} private RestaurantOrder order(UUID id){return ApiSupport.required(orders.findById(id),"Order not found");} private MenuItem item(UUID id){return ApiSupport.required(items.findById(id),"Menu item not found");}
}

@Service class PaymentApiService {
    private final PaymentRepository repo; private final RestaurantOrderRepository orders; PaymentApiService(PaymentRepository repo,RestaurantOrderRepository orders){this.repo=repo;this.orders=orders;}
    List<ApiModels.PaymentResponse> all(){return repo.findAll().stream().map(ApiModels.PaymentResponse::from).toList();}
    ApiModels.PaymentResponse create(ApiModels.PaymentRequest r){if(repo.existsByOrderId(r.orderId()))throw new ResponseStatusException(HttpStatus.CONFLICT,"Order already has a payment");Payment e=new Payment(order(r.orderId()),r.amount(),r.paymentMethod().trim(),r.transactionReference());e.update(order(r.orderId()),r.amount(),r.paymentMethod().trim(),ApiSupport.text(r.paymentStatus(),"PENDING"),r.transactionReference());return ApiModels.PaymentResponse.from(repo.save(e));}
    ApiModels.PaymentResponse update(UUID id,ApiModels.PaymentRequest r){Payment e=ApiSupport.required(repo.findById(id),"Payment not found");e.update(order(r.orderId()),r.amount(),r.paymentMethod().trim(),ApiSupport.text(r.paymentStatus(),"PENDING"),r.transactionReference());return ApiModels.PaymentResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Payment not found");repo.deleteById(id);} private RestaurantOrder order(UUID id){return ApiSupport.required(orders.findById(id),"Order not found");}
}

@Service class ReviewApiService {
    private final ReviewRepository repo; private final UserAccountRepository users; private final MenuItemRepository items; ReviewApiService(ReviewRepository repo,UserAccountRepository users,MenuItemRepository items){this.repo=repo;this.users=users;this.items=items;}
    List<ApiModels.ReviewResponse> all(){return repo.findAll().stream().map(ApiModels.ReviewResponse::from).toList();}
    ApiModels.ReviewResponse create(ApiModels.ReviewRequest r){return ApiModels.ReviewResponse.from(repo.save(new Review(user(r.userId()),item(r.menuItemId()),r.rating(),r.comment())));}
    ApiModels.ReviewResponse update(UUID id,ApiModels.ReviewRequest r){Review e=ApiSupport.required(repo.findById(id),"Review not found");e.update(user(r.userId()),item(r.menuItemId()),r.rating(),r.comment());return ApiModels.ReviewResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Review not found");repo.deleteById(id);} private UserAccount user(UUID id){return ApiSupport.required(users.findById(id),"User not found");} private MenuItem item(UUID id){return ApiSupport.required(items.findById(id),"Menu item not found");}
}

@Service class NotificationApiService {
    private final NotificationRepository repo; private final UserAccountRepository users; NotificationApiService(NotificationRepository repo,UserAccountRepository users){this.repo=repo;this.users=users;}
    List<ApiModels.NotificationResponse> all(){return repo.findAll().stream().map(ApiModels.NotificationResponse::from).toList();}
    ApiModels.NotificationResponse create(ApiModels.NotificationRequest r){return ApiModels.NotificationResponse.from(repo.save(new Notification(user(r.userId()),r.message().trim(),r.read())));}
    ApiModels.NotificationResponse update(UUID id,ApiModels.NotificationRequest r){Notification e=ApiSupport.required(repo.findById(id),"Notification not found");e.update(user(r.userId()),r.message().trim(),r.read());return ApiModels.NotificationResponse.from(repo.save(e));}
    void delete(UUID id){if(!repo.existsById(id))throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Notification not found");repo.deleteById(id);} private UserAccount user(UUID id){return ApiSupport.required(users.findById(id),"User not found");}
}

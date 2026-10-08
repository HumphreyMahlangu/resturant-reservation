package com.maisonverre.reservation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

abstract class ApiController {
    static final String ORIGIN = "${app.cors.allowed-origin:http://localhost:5173}";
}

@RestController @RequestMapping("/api/roles") @CrossOrigin(origins=ApiController.ORIGIN)
class RoleApiController {
    private final RoleApiService service; RoleApiController(RoleApiService service){this.service=service;}
    @GetMapping List<ApiModels.RoleResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.RoleResponse create(@Valid @RequestBody ApiModels.RoleRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.RoleResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.RoleRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/users") @CrossOrigin(origins=ApiController.ORIGIN)
class UserApiController {
    private final UserApiService service; UserApiController(UserApiService service){this.service=service;}
    @GetMapping List<ApiModels.UserResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.UserResponse create(@Valid @RequestBody ApiModels.UserRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.UserResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.UserRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/categories") @CrossOrigin(origins=ApiController.ORIGIN)
class CategoryApiController {
    private final CategoryApiService service; CategoryApiController(CategoryApiService service){this.service=service;}
    @GetMapping List<ApiModels.CategoryResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.CategoryResponse create(@Valid @RequestBody ApiModels.CategoryRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.CategoryResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.CategoryRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/menu-items") @CrossOrigin(origins=ApiController.ORIGIN)
class MenuItemApiController {
    private final MenuItemApiService service; MenuItemApiController(MenuItemApiService service){this.service=service;}
    @GetMapping List<ApiModels.MenuItemResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.MenuItemResponse create(@Valid @RequestBody ApiModels.MenuItemRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.MenuItemResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.MenuItemRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/orders") @CrossOrigin(origins=ApiController.ORIGIN)
class OrderApiController {
    private final OrderApiService service; OrderApiController(OrderApiService service){this.service=service;}
    @GetMapping List<ApiModels.OrderResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.OrderResponse create(@Valid @RequestBody ApiModels.OrderRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.OrderResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.OrderRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/order-items") @CrossOrigin(origins=ApiController.ORIGIN)
class OrderItemApiController {
    private final OrderItemApiService service; OrderItemApiController(OrderItemApiService service){this.service=service;}
    @GetMapping List<ApiModels.OrderItemResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.OrderItemResponse create(@Valid @RequestBody ApiModels.OrderItemRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.OrderItemResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.OrderItemRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/payments") @CrossOrigin(origins=ApiController.ORIGIN)
class PaymentApiController {
    private final PaymentApiService service; PaymentApiController(PaymentApiService service){this.service=service;}
    @GetMapping List<ApiModels.PaymentResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.PaymentResponse create(@Valid @RequestBody ApiModels.PaymentRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.PaymentResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.PaymentRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/reviews") @CrossOrigin(origins=ApiController.ORIGIN)
class ReviewApiController {
    private final ReviewApiService service; ReviewApiController(ReviewApiService service){this.service=service;}
    @GetMapping List<ApiModels.ReviewResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.ReviewResponse create(@Valid @RequestBody ApiModels.ReviewRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.ReviewResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.ReviewRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

@RestController @RequestMapping("/api/notifications") @CrossOrigin(origins=ApiController.ORIGIN)
class NotificationApiController {
    private final NotificationApiService service; NotificationApiController(NotificationApiService service){this.service=service;}
    @GetMapping List<ApiModels.NotificationResponse> all(){return service.all();}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ApiModels.NotificationResponse create(@Valid @RequestBody ApiModels.NotificationRequest r){return service.create(r);}
    @PutMapping("/{id}") ApiModels.NotificationResponse update(@PathVariable UUID id,@Valid @RequestBody ApiModels.NotificationRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void delete(@PathVariable UUID id){service.delete(id);}
}

package com.maisonverre.reservation;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "${app.cors.allowed-origin:http://localhost:5173}")
public class RestaurantTableController {

    private final RestaurantTableService service;

    public RestaurantTableController(RestaurantTableService service) {
        this.service = service;
    }

    @GetMapping
    public List<RestaurantTableResponse> findAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantTableResponse create(@Valid @RequestBody RestaurantTableRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public RestaurantTableResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody RestaurantTableRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}

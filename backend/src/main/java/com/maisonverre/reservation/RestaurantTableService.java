package com.maisonverre.reservation;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RestaurantTableService {

    private final RestaurantTableRepository repository;

    public RestaurantTableService(RestaurantTableRepository repository) {
        this.repository = repository;
    }

    public List<RestaurantTableResponse> findAll() {
        return repository.findAllByOrderByTableNumberAsc().stream()
                .map(RestaurantTableResponse::from)
                .toList();
    }

    public RestaurantTableResponse create(RestaurantTableRequest request) {
        String tableNumber = request.tableNumber().trim();
        if (repository.existsByTableNumber(tableNumber)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table number already exists");
        }
        RestaurantTable table = new RestaurantTable(tableNumber, request.capacity());
        table.update(tableNumber, request.capacity(), normalizeStatus(request.status()));
        return RestaurantTableResponse.from(repository.save(table));
    }

    public RestaurantTableResponse update(UUID id, RestaurantTableRequest request) {
        RestaurantTable table = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found"));
        String tableNumber = request.tableNumber().trim();
        if (repository.existsByTableNumberAndIdNot(tableNumber, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Table number already exists");
        }
        table.update(tableNumber, request.capacity(), normalizeStatus(request.status()));
        return RestaurantTableResponse.from(repository.save(table));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Table not found");
        }
        repository.deleteById(id);
    }

    private String normalizeStatus(String status) {
        return status.trim().toUpperCase();
    }
}

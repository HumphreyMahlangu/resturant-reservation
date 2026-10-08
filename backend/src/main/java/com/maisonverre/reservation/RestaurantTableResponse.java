package com.maisonverre.reservation;

import java.util.UUID;

public record RestaurantTableResponse(
        UUID id,
        String tableNumber,
        int capacity,
        String status
) {
    public static RestaurantTableResponse from(RestaurantTable table) {
        return new RestaurantTableResponse(
                table.getId(),
                table.getTableNumber(),
                table.getCapacity(),
                table.getStatus()
        );
    }
}

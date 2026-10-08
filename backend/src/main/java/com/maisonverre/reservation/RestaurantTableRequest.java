package com.maisonverre.reservation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RestaurantTableRequest(
        @NotBlank @Size(max = 30) String tableNumber,
        @Min(1) @Max(50) int capacity,
        @NotBlank @Size(max = 20) String status
) {
}

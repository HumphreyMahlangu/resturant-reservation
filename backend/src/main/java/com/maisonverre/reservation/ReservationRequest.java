package com.maisonverre.reservation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 40) String phone,
        @NotNull LocalDate date,
        @NotNull LocalTime time,
        @Min(1) @Max(8) int partySize,
        @NotBlank @Size(max = 40) String seating,
        @Size(max = 500) String requests
) {
}

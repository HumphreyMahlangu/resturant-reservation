package com.maisonverre.reservation;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        String reference,
        String name,
        String phone,
        LocalDate date,
        LocalTime time,
        int partySize,
        String seating,
        String requests,
        Instant createdAt
) {
    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReference(),
                reservation.getName(),
                reservation.getPhone(),
                reservation.getDate(),
                reservation.getTime(),
                reservation.getPartySize(),
                reservation.getSeating(),
                reservation.getRequests(),
                reservation.getCreatedAt()
        );
    }
}

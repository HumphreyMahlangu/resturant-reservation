package com.maisonverre.reservation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {
    List<Reservation> findAllByOrderByCreatedAtAsc();
    boolean existsByReference(String reference);
}

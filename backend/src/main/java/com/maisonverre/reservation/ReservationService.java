package com.maisonverre.reservation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public List<ReservationResponse> findAll() {
        return repository.findAllByOrderByCreatedAtAsc().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse create(ReservationRequest request) {
        Reservation reservation = new Reservation(
                nextReference(),
                request.name().trim(),
                request.phone().trim(),
                request.date(),
                request.time(),
                request.partySize(),
                request.seating().trim(),
                normalize(request.requests())
        );
        return ReservationResponse.from(repository.save(reservation));
    }

    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reservation not found");
        }
        repository.deleteById(id);
    }

    private String nextReference() {
        String reference;
        do {
            reference = "MV-" + ThreadLocalRandom.current().nextInt(1000, 10000);
        } while (repository.existsByReference(reference));
        return reference;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}

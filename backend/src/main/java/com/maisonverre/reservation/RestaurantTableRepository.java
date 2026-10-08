package com.maisonverre.reservation;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, UUID> {
    List<RestaurantTable> findAllByOrderByTableNumberAsc();
    boolean existsByTableNumber(String tableNumber);
    boolean existsByTableNumberAndIdNot(String tableNumber, UUID id);
}

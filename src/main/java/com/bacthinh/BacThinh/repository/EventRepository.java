package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e FROM Event e " +
           "WHERE (:keyword IS NULL OR " +
           "       LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "       LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:startTimeFrom IS NULL OR e.startTime >= :startTimeFrom) " +
           "AND (:startTimeTo IS NULL OR e.startTime <= :startTimeTo)")
    Page<Event> searchEvents(@Param("keyword") String keyword, 
                            @Param("startTimeFrom") LocalDateTime startTimeFrom, 
                            @Param("startTimeTo") LocalDateTime startTimeTo, 
                            Pageable pageable);
} 
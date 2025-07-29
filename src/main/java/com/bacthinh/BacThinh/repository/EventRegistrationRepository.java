package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.EventRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    @Query("SELECT er FROM EventRegistration er " +
           "JOIN FETCH er.event e " +
           "JOIN FETCH er.resident r " +
           "LEFT JOIN FETCH er.registeredByUser u " +
           "WHERE (:eventId IS NULL OR er.event.id = :eventId) " +
           "AND (:residentId IS NULL OR er.resident.id = :residentId) " +
           "AND (:registeredByUserId IS NULL OR er.registeredByUser.id = :registeredByUserId)")
    Page<EventRegistration> searchRegistrations(@Param("eventId") Long eventId,
                                               @Param("residentId") Long residentId,
                                               @Param("registeredByUserId") Long registeredByUserId,
                                               Pageable pageable);

    @Query("SELECT COUNT(er) FROM EventRegistration er WHERE er.event.id = :eventId")
    Long countByEventId(@Param("eventId") Long eventId);

    @Query("SELECT er FROM EventRegistration er " +
           "JOIN FETCH er.event e " +
           "JOIN FETCH er.resident r " +
           "LEFT JOIN FETCH er.registeredByUser u " +
           "WHERE er.event.id = :eventId")
    List<EventRegistration> findByEventId(@Param("eventId") Long eventId);
} 
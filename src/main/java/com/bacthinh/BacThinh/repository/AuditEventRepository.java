package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, Long> {

    Page<AuditEvent> findByEntityTypeAndEntityIdOrderByTimestampDesc(
            String entityType, String entityId, Pageable pageable);

    Page<AuditEvent> findByEntityTypeOrderByTimestampDesc(String entityType, Pageable pageable);

    Page<AuditEvent> findAllByOrderByTimestampDesc(Pageable pageable);

    List<AuditEvent> findByTimestampBetweenOrderByTimestampDesc(
            LocalDateTime from, LocalDateTime to);

    @Query("SELECT ae FROM AuditEvent ae WHERE ae.createdBy = :username ORDER BY ae.timestamp DESC")
    List<AuditEvent> findByUser(@Param("username") String username);

    @Query(value = "SELECT * FROM audit_events WHERE DATE(timestamp) = CURRENT_DATE",
            nativeQuery = true)
    List<AuditEvent> findTodayEvents();
}

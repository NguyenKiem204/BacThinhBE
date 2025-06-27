package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.entity.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditEventService {
    void saveAuditEvent(AuditEvent auditEvent);
    Page<AuditEvent> getAuditEvents(String entityType, String entityId, Pageable pageable);
    List<AuditEvent> getAuditEventsByDateRange(LocalDateTime from, LocalDateTime to);
}

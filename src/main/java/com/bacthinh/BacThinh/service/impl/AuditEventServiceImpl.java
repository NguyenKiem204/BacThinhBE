package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.entity.AuditEvent;
import com.bacthinh.BacThinh.repository.AuditEventRepository;
import com.bacthinh.BacThinh.service.AuditEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditEventServiceImpl implements AuditEventService {

    private final AuditEventRepository auditEventRepository;

    @Override
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAuditEvent(AuditEvent auditEvent) {
        try {
            auditEventRepository.save(auditEvent);
            log.debug("Audit event saved: {} for entity: {} with ID: {}",
                    auditEvent.getAction(), auditEvent.getEntityType(), auditEvent.getEntityId());
        } catch (Exception e) {
            log.error("Failed to save audit event", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditEvent> getAuditEvents(String entityType, String entityId, Pageable pageable) {
        if (entityType != null && entityId != null) {
            return auditEventRepository.findByEntityTypeAndEntityIdOrderByTimestampDesc(
                    entityType, entityId, pageable);
        } else if (entityType != null) {
            return auditEventRepository.findByEntityTypeOrderByTimestampDesc(entityType, pageable);
        }
        return auditEventRepository.findAllByOrderByTimestampDesc(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditEvent> getAuditEventsByDateRange(LocalDateTime from, LocalDateTime to) {
        return auditEventRepository.findByTimestampBetweenOrderByTimestampDesc(from, to);
    }
}

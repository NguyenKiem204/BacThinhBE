package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.entity.AuditEvent;
import com.bacthinh.BacThinh.service.AuditEventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Audit", description = "Audit Management APIs: manage system audit logs")
@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventService auditEventService;

    @Operation(summary = "Get audit events with pagination", description = "ADMIN gets audit events, supports filtering by entityType and entityId.")
    @GetMapping("/events")
    public ResponseEntity<Page<AuditEvent>> getAuditEvents(
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) String entityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditEvent> events = auditEventService.getAuditEvents(entityType, entityId, pageable);
        return ResponseEntity.ok(events);
    }

    @Operation(summary = "Get audit events by date range", description = "ADMIN gets audit events within a date range.")
    @GetMapping("/events/date-range")
    public ResponseEntity<List<AuditEvent>> getAuditEventsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {

        List<AuditEvent> events = auditEventService.getAuditEventsByDateRange(from, to);
        return ResponseEntity.ok(events);
    }
}

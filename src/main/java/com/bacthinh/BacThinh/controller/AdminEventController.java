package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.EventRequest;
import com.bacthinh.BacThinh.dto.response.EventResponse;
import com.bacthinh.BacThinh.service.EventService;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Admin Event Controller", description = "Admin manage events: create, update, delete, get, list, search")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @Operation(summary = "Create an Event", description = "ADMIN creates an event.")
    @PostMapping
    public ResponseEntity<ApiResponse<EventResponse>> createEvent(@RequestBody @Valid EventRequest request) {
        EventResponse response = eventService.createEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Event created successfully!"));
    }

    @Operation(summary = "Update an Event", description = "ADMIN updates an event by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> updateEvent(@PathVariable Long id, @RequestBody @Valid EventRequest request) {
        EventResponse response = eventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Event updated successfully!"));
    }

    @Operation(summary = "Delete an Event", description = "ADMIN deletes an event by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Event deleted successfully!"));
    }

    @Operation(summary = "Get Event by ID", description = "Get a specific event by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventResponse>> getEvent(@PathVariable Long id) {
        EventResponse response = eventService.getEvent(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Event retrieved successfully!"));
    }

    @Operation(summary = "Get all events", description = "Get all events with pagination and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventResponse>>> getAllEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventResponse> events = eventService.getAllEvents(pageable);
        return ResponseEntity.ok(ApiResponse.success(events, "Events retrieved successfully!"));
    }

    @Operation(summary = "Search events", description = "Tìm kiếm theo keyword (tên sự kiện hoặc địa điểm) với phân trang")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EventResponse>>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimeTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventResponse> result = eventService.searchEvents(keyword, startTimeFrom, startTimeTo, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Search events successfully!"));
    }
} 
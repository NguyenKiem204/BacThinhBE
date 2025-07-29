package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.EventRegistrationRequest;
import com.bacthinh.BacThinh.dto.response.EventRegistrationResponse;
import com.bacthinh.BacThinh.service.EventRegistrationService;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin Event Registration Controller", description = "Admin manage event registrations: create, update, delete, get, list, search")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/event-registrations")
public class AdminEventRegistrationController {
    private final EventRegistrationService eventRegistrationService;

    @Operation(summary = "Create an Event Registration", description = "ADMIN creates an event registration.")
    @PostMapping
    public ResponseEntity<ApiResponse<EventRegistrationResponse>> createRegistration(@RequestBody @Valid EventRegistrationRequest request) {
        EventRegistrationResponse response = eventRegistrationService.createRegistration(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Event registration created successfully!"));
    }

    @Operation(summary = "Update an Event Registration", description = "ADMIN updates an event registration by ID.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EventRegistrationResponse>> updateRegistration(@PathVariable Long id, @RequestBody @Valid EventRegistrationRequest request) {
        EventRegistrationResponse response = eventRegistrationService.updateRegistration(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Event registration updated successfully!"));
    }

    @Operation(summary = "Delete an Event Registration", description = "ADMIN deletes an event registration by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRegistration(@PathVariable Long id) {
        eventRegistrationService.deleteRegistration(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Event registration deleted successfully!"));
    }

    @Operation(summary = "Get Event Registration by ID", description = "Get a specific event registration by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EventRegistrationResponse>> getRegistration(@PathVariable Long id) {
        EventRegistrationResponse response = eventRegistrationService.getRegistration(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Event registration retrieved successfully!"));
    }

    @Operation(summary = "Get all event registrations", description = "Get all event registrations with pagination and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventRegistrationResponse>>> getAllRegistrations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventRegistrationResponse> registrations = eventRegistrationService.getAllRegistrations(pageable);
        return ResponseEntity.ok(ApiResponse.success(registrations, "Event registrations retrieved successfully!"));
    }

    @Operation(summary = "Search event registrations", description = "Tìm kiếm đăng ký sự kiện theo event, resident, user đăng ký với phân trang")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<EventRegistrationResponse>>> searchRegistrations(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long residentId,
            @RequestParam(required = false) Long registeredByUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<EventRegistrationResponse> result = eventRegistrationService.searchRegistrations(eventId, residentId, registeredByUserId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result, "Search event registrations successfully!"));
    }
} 
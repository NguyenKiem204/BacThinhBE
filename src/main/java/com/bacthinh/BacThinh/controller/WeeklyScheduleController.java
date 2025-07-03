package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.CreateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.UpdateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.WeeklyScheduleSearchRequest;
import com.bacthinh.BacThinh.dto.response.PagedWeeklyScheduleResponse;
import com.bacthinh.BacThinh.dto.response.WeeklyScheduleResponse;
import com.bacthinh.BacThinh.entity.ScheduleStatus;
import com.bacthinh.BacThinh.service.WeeklyScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/weekly-schedules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Weekly Schedule Management", description = "APIs for managing weekly mass schedules")
public class WeeklyScheduleController {

    private final WeeklyScheduleService weeklyScheduleService;

    @PostMapping
    @Operation(summary = "Create a new weekly schedule", description = "Create a new weekly mass schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Weekly schedule created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "409", description = "Weekly schedule already exists for this date range")
    })
    public ResponseEntity<WeeklyScheduleResponse> createWeeklySchedule(
            @Valid @RequestBody CreateWeeklyScheduleRequest request) {
        log.info("Creating weekly schedule: {}", request);
        WeeklyScheduleResponse response = weeklyScheduleService.createWeeklySchedule(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a weekly schedule", description = "Update an existing weekly mass schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedule updated successfully"),
            @ApiResponse(responseCode = "404", description = "Weekly schedule not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<WeeklyScheduleResponse> updateWeeklySchedule(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWeeklyScheduleRequest request) {
        log.info("Updating weekly schedule ID: {} with data: {}", id, request);
        WeeklyScheduleResponse response = weeklyScheduleService.updateWeeklySchedule(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a weekly schedule", description = "Delete a weekly mass schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Weekly schedule deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Weekly schedule not found")
    })
    public ResponseEntity<Void> deleteWeeklySchedule(@PathVariable Long id) {
        log.info("Deleting weekly schedule ID: {}", id);
        weeklyScheduleService.deleteWeeklySchedule(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get weekly schedule by ID", description = "Retrieve a specific weekly mass schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedule found"),
            @ApiResponse(responseCode = "404", description = "Weekly schedule not found")
    })
    public ResponseEntity<WeeklyScheduleResponse> getWeeklyScheduleById(@PathVariable Long id) {
        log.info("Getting weekly schedule ID: {}", id);
        WeeklyScheduleResponse response = weeklyScheduleService.getWeeklyScheduleById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search weekly schedules", description = "Search and filter weekly mass schedules")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<PagedWeeklyScheduleResponse> searchWeeklySchedules(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer weekNumber,
            @RequestParam(required = false) ScheduleStatus status,
            @RequestParam(required = false) String createdBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "weekStartDate") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        WeeklyScheduleSearchRequest request = WeeklyScheduleSearchRequest.builder()
                .startDate(startDate)
                .endDate(endDate)
                .year(year)
                .weekNumber(weekNumber)
                .status(status)
                .createdBy(createdBy)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        log.info("Searching weekly schedules with criteria: {}", request);
        PagedWeeklyScheduleResponse response = weeklyScheduleService.searchWeeklySchedules(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get weekly schedule by date", description = "Get weekly schedule containing a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedule found"),
            @ApiResponse(responseCode = "404", description = "No weekly schedule found for this date")
    })
    public ResponseEntity<WeeklyScheduleResponse> getWeeklyScheduleByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting weekly schedule for date: {}", date);
        WeeklyScheduleResponse response = weeklyScheduleService.getWeeklyScheduleByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Get weekly schedules by date range", description = "Get all weekly schedules within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedules found")
    })
    public ResponseEntity<List<WeeklyScheduleResponse>> getWeeklySchedulesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting weekly schedules for date range: {} - {}", startDate, endDate);
        List<WeeklyScheduleResponse> response = weeklyScheduleService.getWeeklySchedulesByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "Publish weekly schedule", description = "Publish a weekly schedule to make it official")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedule published successfully"),
            @ApiResponse(responseCode = "404", description = "Weekly schedule not found")
    })
    public ResponseEntity<WeeklyScheduleResponse> publishWeeklySchedule(@PathVariable Long id) {
        log.info("Publishing weekly schedule ID: {}", id);
        WeeklyScheduleResponse response = weeklyScheduleService.publishWeeklySchedule(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/archive")
    @Operation(summary = "Archive weekly schedule", description = "Archive a weekly schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Weekly schedule archived successfully"),
            @ApiResponse(responseCode = "404", description = "Weekly schedule not found")
    })
    public ResponseEntity<WeeklyScheduleResponse> archiveWeeklySchedule(@PathVariable Long id) {
        log.info("Archiving weekly schedule ID: {}", id);
        WeeklyScheduleResponse response = weeklyScheduleService.archiveWeeklySchedule(id);
        return ResponseEntity.ok(response);
    }
}

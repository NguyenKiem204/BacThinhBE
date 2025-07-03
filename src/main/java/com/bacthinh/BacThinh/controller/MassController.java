package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.CreateMassRequest;
import com.bacthinh.BacThinh.dto.request.UpdateMassRequest;
import com.bacthinh.BacThinh.dto.response.MassResponse;
import com.bacthinh.BacThinh.service.MassService;
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
@RequestMapping("/api/v1/masses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Mass Management", description = "APIs for managing individual masses")
public class MassController {

    private final MassService massService;

    @PostMapping("/daily-schedule/{dailyScheduleId}")
    @Operation(summary = "Create a new mass", description = "Create a new mass for a specific daily schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mass created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Daily schedule not found")
    })
    public ResponseEntity<MassResponse> createMass(
            @PathVariable Long dailyScheduleId,
            @Valid @RequestBody CreateMassRequest request) {
        log.info("Creating mass for daily schedule ID: {} with data: {}", dailyScheduleId, request);
        MassResponse response = massService.createMass(dailyScheduleId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a mass", description = "Update an existing mass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mass updated successfully"),
            @ApiResponse(responseCode = "404", description = "Mass not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<MassResponse> updateMass(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMassRequest request) {
        log.info("Updating mass ID: {} with data: {}", id, request);
        MassResponse response = massService.updateMass(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a mass", description = "Delete a mass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Mass deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Mass not found")
    })
    public ResponseEntity<Void> deleteMass(@PathVariable Long id) {
        log.info("Deleting mass ID: {}", id);
        massService.deleteMass(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get mass by ID", description = "Retrieve a specific mass")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mass found"),
            @ApiResponse(responseCode = "404", description = "Mass not found")
    })
    public ResponseEntity<MassResponse> getMassById(@PathVariable Long id) {
        log.info("Getting mass ID: {}", id);
        MassResponse response = massService.getMassById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/daily-schedule/{dailyScheduleId}")
    @Operation(summary = "Get masses by daily schedule", description = "Get all masses for a specific daily schedule")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masses found")
    })
    public ResponseEntity<List<MassResponse>> getMassesByDailyScheduleId(@PathVariable Long dailyScheduleId) {
        log.info("Getting masses for daily schedule ID: {}", dailyScheduleId);
        List<MassResponse> response = massService.getMassesByDailyScheduleId(dailyScheduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date")
    @Operation(summary = "Get masses by date", description = "Get all masses for a specific date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masses found")
    })
    public ResponseEntity<List<MassResponse>> getMassesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting masses for date: {}", date);
        List<MassResponse> response = massService.getMassesByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-date-range")
    @Operation(summary = "Get masses by date range", description = "Get all masses within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masses found")
    })
    public ResponseEntity<List<MassResponse>> getMassesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting masses for date range: {} - {}", startDate, endDate);
        List<MassResponse> response = massService.getMassesByDateRange(startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/by-celebrant")
    @Operation(summary = "Get masses by celebrant", description = "Get all masses for a specific celebrant within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masses found")
    })
    public ResponseEntity<List<MassResponse>> getMassesByCelebrant(
            @RequestParam String celebrant,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("Getting masses for celebrant: {} in date range: {} - {}", celebrant, startDate, endDate);
        List<MassResponse> response = massService.getMassesByCelebrant(celebrant, startDate, endDate);
        return ResponseEntity.ok(response);
    }
}

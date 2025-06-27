package com.bacthinh.BacThinh.controller;


import com.bacthinh.BacThinh.dto.request.ResidentCreationRequest;
import com.bacthinh.BacThinh.dto.request.ResidentUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import com.bacthinh.BacThinh.dto.response.ResidentResponse;
import com.bacthinh.BacThinh.service.ResidentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Resident Controller", description = "Manage residents: create, update, search, delete, filter by status")
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/residents")
public class ResidentController {

    private final ResidentService residentService;

    @Operation(summary = "Create a new resident", description = "ADMIN creates a new resident.")
    @PostMapping
    public ResponseEntity<ApiResponse<ResidentResponse>> createResident(@RequestBody @Valid ResidentCreationRequest request) {
        ResidentResponse response = residentService.createResident(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Resident created successfully!"));
    }

    @Operation(summary = "Get resident by ID", description = "Get resident details by ID.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ResidentResponse>> getResidentById(@PathVariable Long id) {
        ResidentResponse response = residentService.getResidentById(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Resident retrieved successfully!"));
    }

    @Operation(summary = "Get all residents", description = "Get all residents with pagination and sorting.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ResidentResponse>>> getAllResidents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ResidentResponse> residents = residentService.getAllResidents(pageable);
        return ResponseEntity.ok(ApiResponse.success(residents, "Residents retrieved successfully!"));
    }

    @Operation(summary = "Search residents", description = "Search residents by keyword.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<ResidentResponse>>> searchResidents(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ResidentResponse> residents = residentService.searchResidents(keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(residents, "Search results retrieved successfully!"));
    }

    @Operation(summary = "Filter residents by baptized status", description = "Filter residents by whether they are baptized.")
    @GetMapping("/baptized")
    public ResponseEntity<ApiResponse<Page<ResidentResponse>>> getResidentsByBaptized(
            @RequestParam Boolean baptized,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ResidentResponse> residents = residentService.getResidentsByBaptized(baptized, pageable);
        return ResponseEntity.ok(ApiResponse.success(residents, "Residents filtered by baptized status!"));
    }

    @Operation(summary = "Filter residents by confirmed status", description = "Filter residents by whether they are confirmed.")
    @GetMapping("/confirmed")
    public ResponseEntity<ApiResponse<Page<ResidentResponse>>> getResidentsByConfirmed(
            @RequestParam Boolean confirmed,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ResidentResponse> residents = residentService.getResidentsByConfirmed(confirmed, pageable);
        return ResponseEntity.ok(ApiResponse.success(residents, "Residents filtered by confirmed status!"));
    }

    @Operation(summary = "Filter residents by married status", description = "Filter residents by whether they are married.")
    @GetMapping("/married")
    public ResponseEntity<ApiResponse<Page<ResidentResponse>>> getResidentsByMarried(
            @RequestParam Boolean married,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ResidentResponse> residents = residentService.getResidentsByMarried(married, pageable);
        return ResponseEntity.ok(ApiResponse.success(residents, "Residents filtered by married status!"));
    }

    @Operation(summary = "Get residents by family ID", description = "Get all residents belonging to a family.")
    @GetMapping("/family/{familyId}")
    public ResponseEntity<ApiResponse<List<ResidentResponse>>> getResidentsByFamilyId(@PathVariable Long familyId) {
        List<ResidentResponse> residents = residentService.getResidentsByFamilyId(familyId);
        return ResponseEntity.ok(ApiResponse.success(residents, "Family members retrieved successfully!"));
    }

    @Operation(summary = "Update resident", description = "ADMIN updates resident information.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ResidentResponse>> updateResident(
            @PathVariable Long id,
            @RequestBody @Valid ResidentUpdateRequest request) {
        ResidentResponse response = residentService.updateResident(id, request);
        return ResponseEntity.ok(ApiResponse.success(response, "Resident updated successfully!"));
    }

    @Operation(summary = "Delete resident", description = "ADMIN deletes a resident by ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteResident(@PathVariable Long id) {
        residentService.deleteResident(id);
        return ResponseEntity.ok(ApiResponse.success("Resident deleted successfully!", "Resident deleted successfully!"));
    }
}

package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.response.EventDetailResponse;
import com.bacthinh.BacThinh.service.EventService;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Event Controller", description = "User view event details with registration information")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    @Operation(summary = "Get Event Detail", description = "Get event detail with registration count and list")
    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<EventDetailResponse>> getEventDetail(@PathVariable Long id) {
        EventDetailResponse response = eventService.getEventDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response, "Event detail retrieved successfully!"));
    }
} 
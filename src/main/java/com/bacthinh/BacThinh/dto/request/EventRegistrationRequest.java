package com.bacthinh.BacThinh.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventRegistrationRequest {
    @NotNull
    private Long eventId;

    @NotNull
    private Long residentId;

    private Long registeredByUserId; // Có thể null nếu resident tự đăng ký

    private String notes;
} 
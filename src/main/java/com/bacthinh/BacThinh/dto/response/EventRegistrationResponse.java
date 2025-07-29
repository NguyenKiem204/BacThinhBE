package com.bacthinh.BacThinh.dto.response;

import com.bacthinh.BacThinh.entity.RegistrationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationResponse {
    private Long id;
    private Long eventId;
    private String eventName;
    private Long residentId;
    private String residentName;
    private Long registeredByUserId;
    private String registeredByUserName;
    private RegistrationStatus status;
    private String notes;
    private LocalDateTime registeredAt;
} 
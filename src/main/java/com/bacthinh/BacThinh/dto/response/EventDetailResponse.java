package com.bacthinh.BacThinh.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    
    // Thông tin đăng ký
    private Long totalRegistrations; // Số cư dân đã đăng ký
    private List<EventRegistrationResponse> registrations; // Danh sách đăng ký
} 
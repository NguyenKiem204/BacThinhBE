package com.bacthinh.BacThinh.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyScheduleResponse {
    private Long id;
    private String dayOfWeek;
    private LocalDate date;
    private List<MassResponse> masses;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package com.bacthinh.BacThinh.dto.response;

import com.bacthinh.BacThinh.entity.ScheduleStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleResponse {
    private Long id;
    private LocalDate weekStartDate;
    private LocalDate weekEndDate;
    private Integer weekNumber;
    private Integer year;
    private String title;
    private String description;
    private List<DailyScheduleResponse> dailySchedules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private ScheduleStatus status;
}

package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.ScheduleStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyScheduleSearchRequest {
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Integer year;
    private Integer weekNumber;
    private ScheduleStatus status;
    private String createdBy;

    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    @Builder.Default
    private String sortBy = "weekStartDate";

    @Builder.Default
    private String sortDirection = "DESC";
}

package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.ScheduleStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateWeeklyScheduleRequest {
    @Size(max = 255, message = "Tiêu đề không được quá 255 ký tự")
    private String title;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;

    @Valid
    private List<UpdateDailyScheduleRequest> dailySchedules;

    private ScheduleStatus status;
}
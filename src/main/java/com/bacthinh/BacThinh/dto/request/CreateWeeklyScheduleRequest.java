package com.bacthinh.BacThinh.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateWeeklyScheduleRequest {
    @NotNull(message = "Ngày bắt đầu tuần không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekStartDate;

    @NotNull(message = "Ngày kết thúc tuần không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate weekEndDate;

    @Size(max = 255, message = "Tiêu đề không được quá 255 ký tự")
    private String title;

    @Size(max = 1000, message = "Mô tả không được quá 1000 ký tự")
    private String description;

    @Valid
    private List<CreateDailyScheduleRequest> dailySchedules;

    private String createdBy;
}

package com.bacthinh.BacThinh.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDailyScheduleRequest {
    private Long id; // null nếu là tạo mới

    @NotBlank(message = "Thứ trong tuần không được để trống")
    private String dayOfWeek;

    @NotNull(message = "Ngày không được để trống")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Valid
    private List<UpdateMassRequest> masses;
}

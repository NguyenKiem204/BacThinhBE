package com.bacthinh.BacThinh.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMassRequest {
    @NotNull(message = "Thời gian không được để trống")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;

    @NotBlank(message = "Loại thánh lễ không được để trống")
    private String type;

    @NotBlank(message = "Celebrant không được để trống")
    private String celebrant;

    private String note;
    private String specialName;

    @Builder.Default
    private Boolean isSolemn = false;
}

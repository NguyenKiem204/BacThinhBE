package com.bacthinh.BacThinh.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MassResponse {
    private Long id;
    private LocalTime time;
    private String type;
    private String celebrant;
    private String note;
    private String specialName;
    private Boolean isSolemn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

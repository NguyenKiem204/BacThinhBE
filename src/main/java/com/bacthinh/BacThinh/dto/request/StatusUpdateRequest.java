package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.ScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
   private ScheduleStatus status;
}

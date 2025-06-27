package com.bacthinh.BacThinh.dto.response;

import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private UserRole role;
    private UserStatus status;
    private Long residentId;
    private LocalDateTime createdAt;
}

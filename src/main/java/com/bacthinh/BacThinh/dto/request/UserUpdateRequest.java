package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.dto.validator.UserStatusSubset;
import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.entity.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.bacthinh.BacThinh.entity.UserStatus.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequest {
    @NotNull(message = "Role is required")
    private UserRole role;

    private Long residentId;

    @UserStatusSubset(anyOf = {ACTIVE, INACTIVE, BLOCKED})
    private UserStatus status;
}

package com.bacthinh.BacThinh.dto.request;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSelfUpdateRequest {
    @Email(message = "Email is invalid")
    private String email;
    private Long residentId;
}


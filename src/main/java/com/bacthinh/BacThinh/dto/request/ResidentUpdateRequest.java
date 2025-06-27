package com.bacthinh.BacThinh.dto.request;

import com.bacthinh.BacThinh.entity.Gender;
import com.bacthinh.BacThinh.entity.RelationToHead;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentUpdateRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    @NotNull(message = "Gender is required")
    private Gender gender;
    
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10-11 digits")
    private String phone;
    
    private String address;
    
    private Boolean baptized;
    
    private Boolean confirmed;
    
    private Boolean married;
    
    @NotNull(message = "Relation to head is required")
    private RelationToHead relationToHead;
    
    private Long familyId;
} 
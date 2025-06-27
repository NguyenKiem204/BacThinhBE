package com.bacthinh.BacThinh.dto.response;

import com.bacthinh.BacThinh.entity.Gender;
import com.bacthinh.BacThinh.entity.RelationToHead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentResponse {
    private Long id;
    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String address;
    private Boolean baptized;
    private Boolean confirmed;
    private Boolean married;
    private RelationToHead relationToHead;
    private Long familyId;
    private String familyCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 
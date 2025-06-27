package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.ResidentCreationRequest;
import com.bacthinh.BacThinh.dto.request.ResidentUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ResidentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ResidentService {
    
    ResidentResponse createResident(ResidentCreationRequest request);
    
    ResidentResponse getResidentById(Long id);
    
    Page<ResidentResponse> getAllResidents(Pageable pageable);
    
    Page<ResidentResponse> searchResidents(String keyword, Pageable pageable);
    
    Page<ResidentResponse> getResidentsByBaptized(Boolean baptized, Pageable pageable);
    
    Page<ResidentResponse> getResidentsByConfirmed(Boolean confirmed, Pageable pageable);
    
    Page<ResidentResponse> getResidentsByMarried(Boolean married, Pageable pageable);
    
    List<ResidentResponse> getResidentsByFamilyId(Long familyId);
    
    ResidentResponse updateResident(Long id, ResidentUpdateRequest request);
    
    void deleteResident(Long id);
} 
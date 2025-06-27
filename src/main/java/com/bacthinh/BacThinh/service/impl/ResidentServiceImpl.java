package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.ResidentCreationRequest;
import com.bacthinh.BacThinh.dto.request.ResidentUpdateRequest;
import com.bacthinh.BacThinh.dto.response.ResidentResponse;
import com.bacthinh.BacThinh.entity.Family;
import com.bacthinh.BacThinh.entity.Resident;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.mapper.ResidentMapper;
import com.bacthinh.BacThinh.repository.FamilyRepository;
import com.bacthinh.BacThinh.repository.ResidentRepository;
import com.bacthinh.BacThinh.service.ResidentService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class ResidentServiceImpl implements ResidentService {

    private final ResidentRepository residentRepository;
    private final FamilyRepository familyRepository;
    private final ResidentMapper residentMapper;

    @Override
    public ResidentResponse createResident(ResidentCreationRequest request) {
        Resident resident = residentMapper.toEntity(request);

        if (request.getFamilyId() != null) {
            Family family = familyRepository.findById(request.getFamilyId())
                    .orElseThrow(() -> new NotFoundException("Family", request.getFamilyId()));
            resident.setFamily(family);
        }

        Resident savedResident = residentRepository.save(resident);
        return residentMapper.toResponse(savedResident);
    }

    @Override
    @Transactional(readOnly = true)
    public ResidentResponse getResidentById(Long id) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resident", id));
        return residentMapper.toResponse(resident);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentResponse> getAllResidents(Pageable pageable) {
        return residentRepository.findAll(pageable)
                .map(residentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentResponse> searchResidents(String keyword, Pageable pageable) {
        return residentRepository.findByKeyword(keyword, pageable)
                .map(residentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentResponse> getResidentsByBaptized(Boolean baptized, Pageable pageable) {
        return residentRepository.findByBaptized(baptized, pageable)
                .map(residentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentResponse> getResidentsByConfirmed(Boolean confirmed, Pageable pageable) {
        return residentRepository.findByConfirmed(confirmed, pageable)
                .map(residentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResidentResponse> getResidentsByMarried(Boolean married, Pageable pageable) {
        return residentRepository.findByMarried(married, pageable)
                .map(residentMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResidentResponse> getResidentsByFamilyId(Long familyId) {
        return residentRepository.findByFamilyId(familyId)
                .stream()
                .map(residentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ResidentResponse updateResident(Long id, ResidentUpdateRequest request) {
        Resident resident = residentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Resident", id));

        residentMapper.updateEntity(resident, request);
        if (request.getFamilyId() != null) {
            Family family = familyRepository.findById(request.getFamilyId())
                    .orElseThrow(() -> new NotFoundException("Family", request.getFamilyId()));
            resident.setFamily(family);
        }

        Resident updatedResident = residentRepository.save(resident);
        return residentMapper.toResponse(updatedResident);
    }

    @Override
    public void deleteResident(Long id) {
        if (!residentRepository.existsById(id)) {
            throw new NotFoundException("Resident", id);
        }
        residentRepository.deleteById(id);
    }
}

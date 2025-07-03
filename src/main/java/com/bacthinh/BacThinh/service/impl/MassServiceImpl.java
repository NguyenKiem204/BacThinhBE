package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.*;
import com.bacthinh.BacThinh.dto.response.MassResponse;
import com.bacthinh.BacThinh.entity.DailySchedule;
import com.bacthinh.BacThinh.entity.Mass;
import com.bacthinh.BacThinh.mapper.MassMapper;
import com.bacthinh.BacThinh.repository.DailyScheduleRepository;
import com.bacthinh.BacThinh.repository.MassRepository;
import com.bacthinh.BacThinh.service.MassService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MassServiceImpl implements MassService {

    private final MassRepository massRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final MassMapper massMapper;

    @Override
    public MassResponse createMass(Long dailyScheduleId, CreateMassRequest request) {
        log.info("Creating mass for daily schedule ID: {}", dailyScheduleId);

        DailySchedule dailySchedule = dailyScheduleRepository.findById(dailyScheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Daily schedule not found with ID: " + dailyScheduleId));

        Mass mass = massMapper.toEntity(request);
        mass.setDailySchedule(dailySchedule);

        Mass savedMass = massRepository.save(mass);

        log.info("Successfully created mass with ID: {}", savedMass.getId());
        return massMapper.toResponse(savedMass);
    }

    @Override
    public MassResponse updateMass(Long massId, UpdateMassRequest request) {
        log.info("Updating mass with ID: {}", massId);

        Mass existingMass = massRepository.findById(massId)
                .orElseThrow(() -> new EntityNotFoundException("Mass not found with ID: " + massId));

        massMapper.updateEntity(existingMass, request);
        Mass updatedMass = massRepository.save(existingMass);

        log.info("Successfully updated mass with ID: {}", massId);
        return massMapper.toResponse(updatedMass);
    }

    @Override
    public void deleteMass(Long massId) {
        log.info("Deleting mass with ID: {}", massId);

        if (!massRepository.existsById(massId)) {
            throw new EntityNotFoundException("Mass not found with ID: " + massId);
        }

        massRepository.deleteById(massId);
        log.info("Successfully deleted mass with ID: {}", massId);
    }

    @Override
    @Transactional(readOnly = true)
    public MassResponse getMassById(Long massId) {
        log.debug("Getting mass with ID: {}", massId);

        Mass mass = massRepository.findById(massId)
                .orElseThrow(() -> new EntityNotFoundException("Mass not found with ID: " + massId));

        return massMapper.toResponse(mass);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MassResponse> getMassesByDailyScheduleId(Long dailyScheduleId) {
        log.debug("Getting masses for daily schedule ID: {}", dailyScheduleId);

        List<Mass> masses = massRepository.findByDailyScheduleId(dailyScheduleId);
        return massMapper.toResponseList(masses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MassResponse> getMassesByDate(LocalDate date) {
        log.debug("Getting masses for date: {}", date);

        List<Mass> masses = massRepository.findByDate(date);
        return massMapper.toResponseList(masses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MassResponse> getMassesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting masses for date range: {} - {}", startDate, endDate);

        List<Mass> masses = massRepository.findByDateRange(startDate, endDate);
        return massMapper.toResponseList(masses);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MassResponse> getMassesByCelebrant(String celebrant, LocalDate startDate, LocalDate endDate) {
        log.debug("Getting masses for celebrant: {} in date range: {} - {}", celebrant, startDate, endDate);

        List<Mass> masses = massRepository.findByCelebrantAndDateRange(celebrant, startDate, endDate);
        return massMapper.toResponseList(masses);
    }
}
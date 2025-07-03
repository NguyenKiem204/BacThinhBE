package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.CreateMassRequest;
import com.bacthinh.BacThinh.dto.request.UpdateMassRequest;
import com.bacthinh.BacThinh.dto.response.MassResponse;

import java.time.LocalDate;
import java.util.List;

public interface MassService {
    MassResponse createMass(Long dailyScheduleId, CreateMassRequest request);
    MassResponse updateMass(Long massId, UpdateMassRequest request);
    void deleteMass(Long massId);
    MassResponse getMassById(Long massId);
    List<MassResponse> getMassesByDailyScheduleId(Long dailyScheduleId);
    List<MassResponse> getMassesByDate(LocalDate date);
    List<MassResponse> getMassesByDateRange(LocalDate startDate, LocalDate endDate);
    List<MassResponse> getMassesByCelebrant(String celebrant, LocalDate startDate, LocalDate endDate);
}

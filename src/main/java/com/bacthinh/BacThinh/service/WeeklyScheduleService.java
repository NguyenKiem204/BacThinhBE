package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.CreateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.UpdateWeeklyScheduleRequest;
import com.bacthinh.BacThinh.dto.request.WeeklyScheduleSearchRequest;
import com.bacthinh.BacThinh.dto.response.PagedWeeklyScheduleResponse;
import com.bacthinh.BacThinh.dto.response.WeeklyScheduleResponse;
import com.bacthinh.BacThinh.entity.ScheduleStatus;

import java.time.LocalDate;
import java.util.List;

public interface WeeklyScheduleService {
    WeeklyScheduleResponse createWeeklySchedule(CreateWeeklyScheduleRequest request);
    WeeklyScheduleResponse updateWeeklySchedule(Long id, UpdateWeeklyScheduleRequest request);
    void deleteWeeklySchedule(Long id);
    WeeklyScheduleResponse getWeeklyScheduleById(Long id);
    PagedWeeklyScheduleResponse searchWeeklySchedules(WeeklyScheduleSearchRequest request);
    WeeklyScheduleResponse getWeeklyScheduleByDate(LocalDate date);
    List<WeeklyScheduleResponse> getWeeklySchedulesByDateRange(LocalDate startDate, LocalDate endDate);
    WeeklyScheduleResponse publishWeeklySchedule(Long id);
    WeeklyScheduleResponse updateStatus(Long id, ScheduleStatus status);
    WeeklyScheduleResponse archiveWeeklySchedule(Long id);
}

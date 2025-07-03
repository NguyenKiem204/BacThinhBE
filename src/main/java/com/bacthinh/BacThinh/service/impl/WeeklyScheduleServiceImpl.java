package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.dto.request.*;
import com.bacthinh.BacThinh.dto.response.PagedWeeklyScheduleResponse;
import com.bacthinh.BacThinh.dto.response.WeeklyScheduleResponse;
import com.bacthinh.BacThinh.entity.DailySchedule;
import com.bacthinh.BacThinh.entity.Mass;
import com.bacthinh.BacThinh.entity.ScheduleStatus;
import com.bacthinh.BacThinh.entity.WeeklySchedule;
import com.bacthinh.BacThinh.exception.NotFoundException;
import com.bacthinh.BacThinh.mapper.DailyScheduleMapper;
import com.bacthinh.BacThinh.mapper.MassMapper;
import com.bacthinh.BacThinh.mapper.WeeklyScheduleMapper;
import com.bacthinh.BacThinh.repository.DailyScheduleRepository;
import com.bacthinh.BacThinh.repository.MassRepository;
import com.bacthinh.BacThinh.repository.WeeklyScheduleRepository;
import com.bacthinh.BacThinh.service.WeeklyScheduleService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WeeklyScheduleServiceImpl implements WeeklyScheduleService {

    private final WeeklyScheduleRepository weeklyScheduleRepository;
    private final DailyScheduleRepository dailyScheduleRepository;
    private final MassRepository massRepository;
    private final WeeklyScheduleMapper weeklyScheduleMapper;
    private final DailyScheduleMapper dailyScheduleMapper;
    private final MassMapper massMapper;

    @Override
    public WeeklyScheduleResponse createWeeklySchedule(CreateWeeklyScheduleRequest request) {
        log.info("Creating weekly schedule for week: {} - {}",
                request.getWeekStartDate(), request.getWeekEndDate());
        validateWeeklyScheduleRequest(request);
        checkForOverlappingSchedules(request.getWeekStartDate(), request.getWeekEndDate(), null);

        WeeklySchedule weeklySchedule = weeklyScheduleMapper.toEntity(request);

        if (request.getDailySchedules() != null) {
            List<DailySchedule> dailySchedules = request.getDailySchedules().stream()
                    .map(dailyRequest -> {
                        DailySchedule dailySchedule = dailyScheduleMapper.toEntity(dailyRequest);
                        dailySchedule.setWeeklySchedule(weeklySchedule);

                        // Create masses for this daily schedule
                        if (dailyRequest.getMasses() != null) {
                            List<Mass> masses = dailyRequest.getMasses().stream()
                                    .map(massRequest -> {
                                        Mass mass = massMapper.toEntity(massRequest);
                                        mass.setDailySchedule(dailySchedule);
                                        return mass;
                                    })
                                    .collect(Collectors.toList());
                            dailySchedule.setMasses(masses);
                        }

                        return dailySchedule;
                    })
                    .collect(Collectors.toList());

            weeklySchedule.setDailySchedules(dailySchedules);
        }

        // Save to database
        WeeklySchedule savedWeeklySchedule = weeklyScheduleRepository.save(weeklySchedule);

        log.info("Successfully created weekly schedule with ID: {}", savedWeeklySchedule.getId());
        return weeklyScheduleMapper.toResponse(savedWeeklySchedule);
    }

    @Override
    public WeeklyScheduleResponse updateWeeklySchedule(Long id, UpdateWeeklyScheduleRequest request) {
        log.info("Updating weekly schedule with ID: {}", id);

        WeeklySchedule existingSchedule = weeklyScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Weekly schedule not found with ID: " + id));

        weeklyScheduleMapper.updateEntity(existingSchedule, request);

        if (request.getDailySchedules() != null) {
            updateDailySchedules(existingSchedule, request.getDailySchedules());
        }

        WeeklySchedule updatedSchedule = weeklyScheduleRepository.save(existingSchedule);

        log.info("Successfully updated weekly schedule with ID: {}", id);
        return weeklyScheduleMapper.toResponse(updatedSchedule);
    }

    @Override
    public void deleteWeeklySchedule(Long id) {
        log.info("Deleting weekly schedule with ID: {}", id);

        if (!weeklyScheduleRepository.existsById(id)) {
            throw new EntityNotFoundException("Weekly schedule not found with ID: " + id);
        }

        weeklyScheduleRepository.deleteById(id);
        log.info("Successfully deleted weekly schedule with ID: {}", id);
    }

    @Override
    public WeeklyScheduleResponse getWeeklyScheduleById(Long id) {
        WeeklySchedule weeklySchedule = weeklyScheduleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Week", id));

        return weeklyScheduleMapper.toResponse(weeklySchedule);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedWeeklyScheduleResponse searchWeeklySchedules(WeeklyScheduleSearchRequest request) {
        log.debug("Searching weekly schedules with criteria: {}", request);
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(request.getSortDirection()) ?
                        Sort.Direction.DESC : Sort.Direction.ASC,
                request.getSortBy()
        );
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Page<WeeklySchedule> page = weeklyScheduleRepository.findBySearchCriteria(
                request.getStartDate(),
                request.getEndDate(),
                request.getYear(),
                request.getWeekNumber(),
                request.getStatus(),
                request.getCreatedBy(),
                pageable
        );

        return weeklyScheduleMapper.toPagedResponse(page);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyScheduleResponse getWeeklyScheduleByDate(LocalDate date) {
        log.debug("Getting weekly schedule for date: {}", date);

        WeeklySchedule schedule = weeklyScheduleRepository.findByDate(date)
                .orElseThrow(() -> new EntityNotFoundException("No weekly schedule found for date: " + date));

        return weeklyScheduleMapper.toResponse(schedule);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WeeklyScheduleResponse> getWeeklySchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Getting weekly schedules for date range: {} - {}", startDate, endDate);

        List<WeeklySchedule> schedules = weeklyScheduleRepository.findByDateRange(startDate, endDate);
        return weeklyScheduleMapper.toResponseList(schedules);
    }

    @Override
    public WeeklyScheduleResponse publishWeeklySchedule(Long id) {
        log.info("Publishing weekly schedule with ID: {}", id);

        WeeklySchedule schedule = weeklyScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Weekly schedule not found with ID: " + id));

        schedule.setStatus(ScheduleStatus.PUBLISHED);
        WeeklySchedule updatedSchedule = weeklyScheduleRepository.save(schedule);

        log.info("Successfully published weekly schedule with ID: {}", id);
        return weeklyScheduleMapper.toResponse(updatedSchedule);
    }

    @Override
    public WeeklyScheduleResponse archiveWeeklySchedule(Long id) {
        log.info("Archiving weekly schedule with ID: {}", id);

        WeeklySchedule schedule = weeklyScheduleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Weekly schedule not found with ID: " + id));

        schedule.setStatus(ScheduleStatus.ARCHIVED);
        WeeklySchedule updatedSchedule = weeklyScheduleRepository.save(schedule);

        log.info("Successfully archived weekly schedule with ID: {}", id);
        return weeklyScheduleMapper.toResponse(updatedSchedule);
    }

    // ================== PRIVATE HELPER METHODS ==================

    private void validateWeeklyScheduleRequest(CreateWeeklyScheduleRequest request) {
        if (request.getWeekStartDate().isAfter(request.getWeekEndDate())) {
            throw new IllegalArgumentException("Week start date must be before or equal to week end date");
        }
        long daysBetween = ChronoUnit.DAYS.between(request.getWeekStartDate(), request.getWeekEndDate());
        if (daysBetween != 6) {
            throw new IllegalArgumentException("Weekly schedule must span exactly 7 days");
        }

        // Validate that start date is Monday
        if (request.getWeekStartDate().getDayOfWeek() != DayOfWeek.MONDAY) {
            throw new IllegalArgumentException("Week must start on Monday");
        }

        // Validate daily schedules dates
        if (request.getDailySchedules() != null) {
            for (CreateDailyScheduleRequest dailyRequest : request.getDailySchedules()) {
                if (dailyRequest.getDate().isBefore(request.getWeekStartDate()) ||
                        dailyRequest.getDate().isAfter(request.getWeekEndDate())) {
                    throw new IllegalArgumentException("Daily schedule date must be within the week range");
                }
            }
        }
    }

    private void checkForOverlappingSchedules(LocalDate startDate, LocalDate endDate, Long excludeId) {
        boolean hasOverlap = weeklyScheduleRepository.existsOverlappingSchedule(startDate, endDate, excludeId);
        if (hasOverlap) {
            throw new IllegalArgumentException("There is already a weekly schedule for this date range");
        }
    }

    private void updateDailySchedules(WeeklySchedule weeklySchedule, List<UpdateDailyScheduleRequest> dailyRequests) {
        // Create map of existing daily schedules
        Map<Long, DailySchedule> existingDailySchedules = weeklySchedule.getDailySchedules().stream()
                .filter(ds -> ds.getId() != null)
                .collect(Collectors.toMap(DailySchedule::getId, Function.identity()));

        // Clear existing daily schedules
        weeklySchedule.getDailySchedules().clear();

        // Process each daily request
        for (UpdateDailyScheduleRequest dailyRequest : dailyRequests) {
            DailySchedule dailySchedule;

            if (dailyRequest.getId() != null && existingDailySchedules.containsKey(dailyRequest.getId())) {
                // Update existing daily schedule
                dailySchedule = existingDailySchedules.get(dailyRequest.getId());
                dailyScheduleMapper.updateEntity(dailySchedule, dailyRequest);
            } else {
                // Create new daily schedule
                dailySchedule = dailyScheduleMapper.toEntity(
                        CreateDailyScheduleRequest.builder()
                                .dayOfWeek(dailyRequest.getDayOfWeek())
                                .date(dailyRequest.getDate())
                                .masses(dailyRequest.getMasses() != null ?
                                        dailyRequest.getMasses().stream()
                                                .map(this::convertUpdateMassToCreateMass)
                                                .collect(Collectors.toList()) : null)
                                .build()
                );
                dailySchedule.setWeeklySchedule(weeklySchedule);
            }
            if (dailyRequest.getMasses() != null) {
                dailyScheduleMapper.mergeMasses(dailySchedule, dailyRequest.getMasses());
            }

            weeklySchedule.getDailySchedules().add(dailySchedule);
        }
    }

    private CreateMassRequest convertUpdateMassToCreateMass(UpdateMassRequest updateRequest) {
        return CreateMassRequest.builder()
                .time(updateRequest.getTime())
                .type(updateRequest.getType())
                .celebrant(updateRequest.getCelebrant())
                .note(updateRequest.getNote())
                .specialName(updateRequest.getSpecialName())
                .isSolemn(updateRequest.getIsSolemn())
                .build();
    }
}
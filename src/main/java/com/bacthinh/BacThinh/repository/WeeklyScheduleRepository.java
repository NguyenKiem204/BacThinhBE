package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.ScheduleStatus;
import com.bacthinh.BacThinh.entity.WeeklySchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyScheduleRepository extends JpaRepository<WeeklySchedule, Long> {

    @Query("SELECT ws FROM WeeklySchedule ws WHERE " +
            "(:startDate IS NULL OR ws.weekStartDate >= :startDate) AND " +
            "(:endDate IS NULL OR ws.weekEndDate <= :endDate) AND " +
            "(:year IS NULL OR ws.year = :year) AND " +
            "(:weekNumber IS NULL OR ws.weekNumber = :weekNumber) AND " +
            "(:status IS NULL OR ws.status = :status) AND " +
            "(:createdBy IS NULL OR ws.createdBy = :createdBy)")
    Page<WeeklySchedule> findBySearchCriteria(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("year") Integer year,
            @Param("weekNumber") Integer weekNumber,
            @Param("status") ScheduleStatus status,
            @Param("createdBy") String createdBy,
            Pageable pageable
    );

    @Query("SELECT ws FROM WeeklySchedule ws WHERE " +
            "ws.weekStartDate <= :date AND ws.weekEndDate >= :date")
    Optional<WeeklySchedule> findByDate(@Param("date") LocalDate date);

    @Query("SELECT ws FROM WeeklySchedule ws WHERE " +
            "ws.year = :year AND ws.weekNumber = :weekNumber")
    Optional<WeeklySchedule> findByYearAndWeekNumber(
            @Param("year") Integer year,
            @Param("weekNumber") Integer weekNumber
    );

    @Query("SELECT ws FROM WeeklySchedule ws WHERE " +
            "ws.weekStartDate >= :startDate AND ws.weekEndDate <= :endDate " +
            "ORDER BY ws.weekStartDate ASC")
    List<WeeklySchedule> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT COUNT(ws) > 0 FROM WeeklySchedule ws WHERE " +
            "ws.weekStartDate <= :endDate AND ws.weekEndDate >= :startDate AND " +
            "(:excludeId IS NULL OR ws.id != :excludeId)")
    boolean existsOverlappingSchedule(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("excludeId") Long excludeId
    );
}

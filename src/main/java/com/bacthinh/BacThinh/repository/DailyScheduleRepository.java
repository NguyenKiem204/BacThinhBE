package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.DailySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyScheduleRepository extends JpaRepository<DailySchedule, Long> {

    @Query("SELECT ds FROM DailySchedule ds WHERE " +
            "ds.weeklySchedule.id = :weeklyScheduleId " +
            "ORDER BY ds.date ASC")
    List<DailySchedule> findByWeeklyScheduleId(@Param("weeklyScheduleId") Long weeklyScheduleId);

    @Query("SELECT ds FROM DailySchedule ds WHERE " +
            "ds.date = :date")
    Optional<DailySchedule> findByDate(@Param("date") LocalDate date);

    @Query("SELECT ds FROM DailySchedule ds WHERE " +
            "ds.date >= :startDate AND ds.date <= :endDate " +
            "ORDER BY ds.date ASC")
    List<DailySchedule> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

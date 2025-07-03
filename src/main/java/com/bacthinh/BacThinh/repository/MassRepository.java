package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.Mass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MassRepository extends JpaRepository<Mass, Long> {

    @Query("SELECT m FROM Mass m WHERE " +
            "m.dailySchedule.id = :dailyScheduleId " +
            "ORDER BY m.time ASC")
    List<Mass> findByDailyScheduleId(@Param("dailyScheduleId") Long dailyScheduleId);

    @Query("SELECT m FROM Mass m WHERE " +
            "m.dailySchedule.date = :date " +
            "ORDER BY m.time ASC")
    List<Mass> findByDate(@Param("date") LocalDate date);

    @Query("SELECT m FROM Mass m WHERE " +
            "m.dailySchedule.date >= :startDate AND m.dailySchedule.date <= :endDate " +
            "ORDER BY m.dailySchedule.date ASC, m.time ASC")
    List<Mass> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT m FROM Mass m WHERE " +
            "m.celebrant = :celebrant AND " +
            "m.dailySchedule.date >= :startDate AND m.dailySchedule.date <= :endDate " +
            "ORDER BY m.dailySchedule.date ASC, m.time ASC")
    List<Mass> findByCelebrantAndDateRange(
            @Param("celebrant") String celebrant,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}

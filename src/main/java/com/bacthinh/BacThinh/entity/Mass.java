package com.bacthinh.BacThinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "masses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    @Column(name = "type", nullable = false)
    private String type; // "Thánh lễ sáng", "Thánh lễ trọng", "Thánh lễ tối"

    @Column(name = "celebrant", nullable = false)
    private String celebrant;

    @Column(name = "note")
    private String note;

    @Column(name = "special_name")
    private String specialName; // Tên lễ đặc biệt

    @Column(name = "is_solemn")
    @Builder.Default
    private Boolean isSolemn = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_schedule_id", nullable = false)
    private DailySchedule dailySchedule;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

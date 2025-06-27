package com.bacthinh.BacThinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "liturgical_readings")
public class LiturgicalReading {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date", unique = true, nullable = false)
    private LocalDate date;

    @Column(name = "reading_1", columnDefinition = "TEXT")
    private String reading1;

    @Column(name = "psalm", columnDefinition = "TEXT")
    private String psalm;

    @Column(name = "reading_2", columnDefinition = "TEXT")
    private String reading2;

    @Column(name = "gospel", columnDefinition = "TEXT")
    private String gospel;

    @Column(name = "season")
    private String season;

    @Column(name = "feast_day")
    private String feastDay;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

package com.bacthinh.BacThinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "families")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Family {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_code", unique = true, nullable = false)
    private String familyCode;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "zone")
    private String zone;

    @JoinColumn(name = "head_id")
    @OneToOne
    private Resident head;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Resident> members = new HashSet<>();


}

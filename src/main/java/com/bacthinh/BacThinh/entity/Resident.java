package com.bacthinh.BacThinh.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "residents")
public class Resident extends BaseAuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "dob")
    private LocalDate dateOfBirth;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "baptized", nullable = false)
    private Boolean baptized = false;

    @Column(name = "confirmed", nullable = false)
    private Boolean confirmed = false;

    @Column(name = "married", nullable = false)
    private Boolean married = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_to_head")
    private RelationToHead relationToHead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id")
    private Family family;

    @OneToOne(mappedBy = "resident", cascade = CascadeType.ALL)
    private User user;

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    private Set<Sacrament> sacraments = new HashSet<>();

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    private Set<EventRegistration> eventRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "resident", cascade = CascadeType.ALL)
    private Set<GroupMember> groupMemberships = new HashSet<>();


}

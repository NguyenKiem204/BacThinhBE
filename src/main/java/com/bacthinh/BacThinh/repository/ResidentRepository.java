package com.bacthinh.BacThinh.repository;

import com.bacthinh.BacThinh.entity.Resident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResidentRepository extends JpaRepository<Resident, Long> {

    Optional<Resident> findByPhone(String phone);

    List<Resident> findByFamilyId(Long familyId);

    @Query("""
                SELECT r FROM Resident r
                WHERE LOWER(r.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(r.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Resident> findByKeyword(@Param("keyword") String keyword, Pageable pageable);


    @Query("SELECT r FROM Resident r WHERE r.baptized = :baptized")
    Page<Resident> findByBaptized(@Param("baptized") Boolean baptized, Pageable pageable);

    @Query("SELECT r FROM Resident r WHERE r.confirmed = :confirmed")
    Page<Resident> findByConfirmed(@Param("confirmed") Boolean confirmed, Pageable pageable);

    @Query("SELECT r FROM Resident r WHERE r.married = :married")
    Page<Resident> findByMarried(@Param("married") Boolean married, Pageable pageable);
} 
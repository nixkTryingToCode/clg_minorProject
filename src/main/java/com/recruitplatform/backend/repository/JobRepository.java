package com.recruitplatform.backend.repository;

import com.recruitplatform.backend.entity.Job;
import com.recruitplatform.backend.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID> {

    List<Job> findByRecruiterId(UUID recruiterId);

    @Query("""
            SELECT j FROM Job j
            WHERE j.status = :status
              AND (:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%')))
              AND (:keyword IS NULL
                   OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                   OR LOWER(j.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Job> search(@Param("status") JobStatus status,
                     @Param("location") String location,
                     @Param("keyword") String keyword,
                     Pageable pageable);
}
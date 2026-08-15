package com.recruitplatform.backend.repository;

import com.recruitplatform.backend.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, UUID> {
}
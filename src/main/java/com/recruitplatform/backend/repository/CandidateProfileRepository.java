package com.recruitplatform.backend.repository;

import com.recruitplatform.backend.entity.CandidateProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CandidateProfileRepository extends JpaRepository<CandidateProfile, UUID> {
}
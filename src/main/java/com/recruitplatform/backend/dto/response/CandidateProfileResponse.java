package com.recruitplatform.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class CandidateProfileResponse {
    private UUID userId;
    private String location;
    private List<String> preferredRoles;
    private String experienceLevel;
    private Map<String, Object> preferences;
    private Instant createdAt;
    private Instant updatedAt;
}
package com.recruitplatform.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class RecruiterProfileResponse {
    private UUID userId;
    private UUID companyId;
    private String companyName;
    private String designation;
    private Map<String, Object> contactInfo;
    private Instant createdAt;
    private Instant updatedAt;
}
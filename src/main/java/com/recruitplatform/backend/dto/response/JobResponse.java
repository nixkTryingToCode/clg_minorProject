package com.recruitplatform.backend.dto.response;

import com.recruitplatform.backend.enums.JobSource;
import com.recruitplatform.backend.enums.JobStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class JobResponse {
    private UUID id;
    private UUID recruiterId;
    private JobSource source;
    private String sourceJobId;
    private String title;
    private String companyName;
    private String description;
    private List<String> requiredSkills;
    private String location;
    private String employmentType;
    private JobStatus status;
    private Instant createdAt;
    private Instant updatedAt;
}
package com.recruitplatform.backend.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class CompanyResponse {
    private UUID id;
    private String name;
    private String description;
    private String website;
    private String location;
    private Instant createdAt;
}
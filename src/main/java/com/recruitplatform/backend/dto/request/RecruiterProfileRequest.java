package com.recruitplatform.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class RecruiterProfileRequest {

    private UUID companyId;

    @Size(max = 255)
    private String designation;

    private Map<String, Object> contactInfo;
}
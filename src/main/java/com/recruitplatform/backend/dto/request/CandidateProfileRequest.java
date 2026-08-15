package com.recruitplatform.backend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CandidateProfileRequest {

    @Size(max = 255)
    private String location;

    private List<@Size(max = 100) String> preferredRoles;

    @Size(max = 50)
    private String experienceLevel;

    private Map<String, Object> preferences;
}
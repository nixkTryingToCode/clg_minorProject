package com.recruitplatform.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitplatform.backend.dto.request.RecruiterProfileRequest;
import com.recruitplatform.backend.dto.response.RecruiterProfileResponse;
import com.recruitplatform.backend.entity.Company;
import com.recruitplatform.backend.entity.RecruiterProfile;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CompanyRepository;
import com.recruitplatform.backend.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterProfileRepository;
    private final CompanyRepository companyRepository;
    private final ObjectMapper objectMapper;

    public RecruiterProfileResponse getProfile(UUID userId) {
        return toResponse(findProfile(userId));
    }

    public RecruiterProfileResponse updateProfile(UUID userId, RecruiterProfileRequest request) {
        RecruiterProfile profile = findProfile(userId);
        profile.setCompanyId(request.getCompanyId());
        profile.setDesignation(request.getDesignation());
        profile.setContactInfo(toJson(request.getContactInfo()));
        return toResponse(recruiterProfileRepository.save(profile));
    }

    private RecruiterProfile findProfile(UUID userId) {
        return recruiterProfileRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Recruiter profile not found", HttpStatus.NOT_FOUND));
    }

    private String toJson(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ApiException("Invalid contact info format", HttpStatus.BAD_REQUEST);
        }
    }

    private Map<String, Object> toMap(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private RecruiterProfileResponse toResponse(RecruiterProfile profile) {
        String companyName = null;
        if (profile.getCompanyId() != null) {
            companyName = companyRepository.findById(profile.getCompanyId())
                    .map(Company::getName)
                    .orElse(null);
        }
        return RecruiterProfileResponse.builder()
                .userId(profile.getUserId())
                .companyId(profile.getCompanyId())
                .companyName(companyName)
                .designation(profile.getDesignation())
                .contactInfo(toMap(profile.getContactInfo()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
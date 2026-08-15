package com.recruitplatform.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitplatform.backend.dto.request.CandidateProfileRequest;
import com.recruitplatform.backend.dto.response.CandidateProfileResponse;
import com.recruitplatform.backend.entity.CandidateProfile;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CandidateProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateProfileService {

    private final CandidateProfileRepository candidateProfileRepository;
    private final ObjectMapper objectMapper;

    public CandidateProfileResponse getProfile(UUID userId) {
        return toResponse(findProfile(userId));
    }

    public CandidateProfileResponse updateProfile(UUID userId, CandidateProfileRequest request) {
        CandidateProfile profile = findProfile(userId);
        profile.setLocation(request.getLocation());
        profile.setPreferredRoles(request.getPreferredRoles());
        profile.setExperienceLevel(request.getExperienceLevel());
        profile.setPreferences(toJson(request.getPreferences()));
        return toResponse(candidateProfileRepository.save(profile));
    }

    private CandidateProfile findProfile(UUID userId) {
        return candidateProfileRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Candidate profile not found", HttpStatus.NOT_FOUND));
    }

    private String toJson(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new ApiException("Invalid preferences format", HttpStatus.BAD_REQUEST);
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

    private CandidateProfileResponse toResponse(CandidateProfile profile) {
        return CandidateProfileResponse.builder()
                .userId(profile.getUserId())
                .location(profile.getLocation())
                .preferredRoles(profile.getPreferredRoles())
                .experienceLevel(profile.getExperienceLevel())
                .preferences(toMap(profile.getPreferences()))
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }
}
package com.recruitplatform.backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitplatform.backend.dto.request.CandidateProfileRequest;
import com.recruitplatform.backend.dto.response.CandidateProfileResponse;
import com.recruitplatform.backend.entity.CandidateProfile;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CandidateProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateProfileServiceTest {

    @Mock
    private CandidateProfileRepository candidateProfileRepository;

    private CandidateProfileService candidateProfileService;

    @BeforeEach
    void setUp() {
        candidateProfileService = new CandidateProfileService(candidateProfileRepository, new ObjectMapper());
    }

    private CandidateProfile profile(UUID userId) {
        CandidateProfile profile = new CandidateProfile();
        profile.setUserId(userId);
        profile.setLocation("Dhaka");
        profile.setPreferredRoles(List.of("Java Developer"));
        profile.setExperienceLevel("JUNIOR");
        profile.setPreferences("{\"remote\":true}");
        profile.setCreatedAt(Instant.now());
        profile.setUpdatedAt(Instant.now());
        return profile;
    }

    @Test
    void getProfileMapsEntityToResponse() {
        UUID userId = UUID.randomUUID();
        when(candidateProfileRepository.findById(userId)).thenReturn(Optional.of(profile(userId)));

        CandidateProfileResponse response = candidateProfileService.getProfile(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getLocation()).isEqualTo("Dhaka");
        assertThat(response.getPreferredRoles()).containsExactly("Java Developer");
        assertThat(response.getExperienceLevel()).isEqualTo("JUNIOR");
        assertThat(response.getPreferences()).containsEntry("remote", true);
    }

    @Test
    void getProfileThrowsNotFoundWhenMissing() {
        UUID userId = UUID.randomUUID();
        when(candidateProfileRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> candidateProfileService.getProfile(userId))
                .isInstanceOf(ApiException.class)
                .hasMessage("Candidate profile not found");
    }

    @Test
    void updateProfilePersistsChangesAndSerializesPreferences() throws Exception {
        UUID userId = UUID.randomUUID();
        when(candidateProfileRepository.findById(userId)).thenReturn(Optional.of(profile(userId)));
        when(candidateProfileRepository.save(any(CandidateProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        CandidateProfileRequest request = new CandidateProfileRequest();
        request.setLocation("New York");
        request.setPreferredRoles(List.of("Backend Engineer"));
        request.setExperienceLevel("MID");
        request.setPreferences(Map.of("remote", false));

        candidateProfileService.updateProfile(userId, request);

        ArgumentCaptor<CandidateProfile> captor = ArgumentCaptor.forClass(CandidateProfile.class);
        verify(candidateProfileRepository).save(captor.capture());
        CandidateProfile saved = captor.getValue();
        assertThat(saved.getLocation()).isEqualTo("New York");
        assertThat(saved.getExperienceLevel()).isEqualTo("MID");
        Map<String, Object> prefs = new ObjectMapper().readValue(saved.getPreferences(), new TypeReference<>() {
        });
        assertThat(prefs).containsEntry("remote", false);
    }
}
package com.recruitplatform.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruitplatform.backend.dto.request.RecruiterProfileRequest;
import com.recruitplatform.backend.dto.response.RecruiterProfileResponse;
import com.recruitplatform.backend.entity.Company;
import com.recruitplatform.backend.entity.RecruiterProfile;
import com.recruitplatform.backend.repository.CompanyRepository;
import com.recruitplatform.backend.repository.RecruiterProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecruiterProfileServiceTest {

    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;
    @Mock
    private CompanyRepository companyRepository;

    private RecruiterProfileService recruiterProfileService;

    @BeforeEach
    void setUp() {
        recruiterProfileService = new RecruiterProfileService(recruiterProfileRepository, companyRepository, new ObjectMapper());
    }

    @Test
    void getProfileIncludesCompanyName() {
        UUID userId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserId(userId);
        profile.setCompanyId(companyId);
        profile.setDesignation("Talent Lead");
        profile.setContactInfo("{\"phone\":\"123\"}");

        Company company = Company.builder().id(companyId).name("Acme Inc").build();

        when(recruiterProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

        RecruiterProfileResponse response = recruiterProfileService.getProfile(userId);

        assertThat(response.getCompanyId()).isEqualTo(companyId);
        assertThat(response.getCompanyName()).isEqualTo("Acme Inc");
        assertThat(response.getDesignation()).isEqualTo("Talent Lead");
        assertThat(response.getContactInfo()).containsEntry("phone", "123");
    }

    @Test
    void updateProfilePersistsContactInfo() {
        UUID userId = UUID.randomUUID();

        RecruiterProfile profile = new RecruiterProfile();
        profile.setUserId(userId);

        when(recruiterProfileRepository.findById(userId)).thenReturn(Optional.of(profile));
        when(recruiterProfileRepository.save(any(RecruiterProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        RecruiterProfileRequest request = new RecruiterProfileRequest();
        request.setDesignation("HR Manager");
        request.setContactInfo(Map.of("email", "hr@acme.com"));

        RecruiterProfileResponse response = recruiterProfileService.updateProfile(userId, request);

        assertThat(response.getDesignation()).isEqualTo("HR Manager");
        assertThat(response.getContactInfo()).containsEntry("email", "hr@acme.com");
    }
}
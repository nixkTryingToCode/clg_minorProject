package com.recruitplatform.backend.controller;

import com.recruitplatform.backend.dto.request.CompanyRequest;
import com.recruitplatform.backend.dto.request.RecruiterProfileRequest;
import com.recruitplatform.backend.dto.response.ApiResponse;
import com.recruitplatform.backend.dto.response.CompanyResponse;
import com.recruitplatform.backend.dto.response.JobResponse;
import com.recruitplatform.backend.dto.response.RecruiterProfileResponse;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.UserRepository;
import com.recruitplatform.backend.service.CompanyService;
import com.recruitplatform.backend.service.JobService;
import com.recruitplatform.backend.service.RecruiterProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recruiters")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterProfileService recruiterProfileService;
    private final CompanyService companyService;
    private final JobService jobService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(recruiterProfileService.getProfile(currentUserId(authentication))));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<RecruiterProfileResponse>> updateMyProfile(
            @Valid @RequestBody RecruiterProfileRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(recruiterProfileService.updateProfile(currentUserId(authentication), request)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/me/jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> listMyJobs(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.listRecruiterJobs(currentUserId(authentication))));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping("/companies")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(@Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.createCompany(request)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<CompanyResponse>>> listCompanies() {
        return ResponseEntity.ok(ApiResponse.ok(companyService.listCompanies()));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @GetMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getCompany(id)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(@PathVariable UUID id,
                                                                      @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.updateCompany(id, request)));
    }

    private UUID currentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }
}
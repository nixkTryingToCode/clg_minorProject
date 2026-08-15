package com.recruitplatform.backend.controller;

import com.recruitplatform.backend.dto.request.JobRequest;
import com.recruitplatform.backend.dto.response.ApiResponse;
import com.recruitplatform.backend.dto.response.JobResponse;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.enums.JobStatus;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.UserRepository;
import com.recruitplatform.backend.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {

    private final JobService jobService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<JobResponse>>> listJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 100), Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(ApiResponse.ok(jobService.listPublished(location, q, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getJob(id)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @Valid @RequestBody JobRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.createJob(currentUserId(authentication), request)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @PathVariable UUID id, @Valid @RequestBody JobRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.updateJob(currentUserId(authentication), id, request)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<JobResponse>> changeStatus(
            @PathVariable UUID id, @RequestParam JobStatus status, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.changeStatus(currentUserId(authentication), id, status)));
    }

    @PreAuthorize("hasRole('RECRUITER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable UUID id, Authentication authentication) {
        jobService.deleteJob(currentUserId(authentication), id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private UUID currentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }
}
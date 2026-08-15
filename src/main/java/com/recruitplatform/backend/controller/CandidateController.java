package com.recruitplatform.backend.controller;

import com.recruitplatform.backend.dto.request.CandidateProfileRequest;
import com.recruitplatform.backend.dto.response.ApiResponse;
import com.recruitplatform.backend.dto.response.CandidateProfileResponse;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.UserRepository;
import com.recruitplatform.backend.service.CandidateProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateProfileService candidateProfileService;
    private final UserRepository userRepository;

    @PreAuthorize("hasRole('CANDIDATE')")
    @GetMapping("/me/profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> getMyProfile(Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(candidateProfileService.getProfile(currentUserId(authentication))));
    }

    @PreAuthorize("hasRole('CANDIDATE')")
    @PutMapping("/me/profile")
    public ResponseEntity<ApiResponse<CandidateProfileResponse>> updateMyProfile(
            @Valid @RequestBody CandidateProfileRequest request, Authentication authentication) {
        return ResponseEntity.ok(ApiResponse.ok(candidateProfileService.updateProfile(currentUserId(authentication), request)));
    }

    private UUID currentUserId(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        return user.getId();
    }
}
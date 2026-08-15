package com.recruitplatform.backend.controller;

import com.recruitplatform.backend.dto.response.ApiResponse;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Example of the RBAC pattern (from Responsibilities doc item 1):
// - /me : any authenticated user
// - /admin/ping : ADMIN only, enforced with @PreAuthorize
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Object>> me(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow();
        return ResponseEntity.ok(ApiResponse.ok(java.util.Map.of(
                "email", user.getEmail(),
                "name", user.getName(),
                "role", user.getRole()
        )));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/ping")
    public ResponseEntity<ApiResponse<String>> adminPing() {
        return ResponseEntity.ok(ApiResponse.ok("pong - you are an admin"));
    }
}

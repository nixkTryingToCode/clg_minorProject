package com.recruitplatform.backend.service;

import com.recruitplatform.backend.dto.request.LoginRequest;
import com.recruitplatform.backend.dto.request.RegisterRequest;
import com.recruitplatform.backend.dto.response.AuthResponse;
import com.recruitplatform.backend.entity.CandidateProfile;
import com.recruitplatform.backend.entity.RecruiterProfile;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.enums.Role;
import com.recruitplatform.backend.exception.ApiException;
import com.recruitplatform.backend.repository.CandidateProfileRepository;
import com.recruitplatform.backend.repository.RecruiterProfileRepository;
import com.recruitplatform.backend.repository.UserRepository;
import com.recruitplatform.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CandidateProfileRepository candidateProfileRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("An account with this email already exists", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .active(true)
                .build();

        userRepository.save(user);
        createProfileFor(user);

        String token = jwtUtil.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }

    private void createProfileFor(User user) {
        if (user.getRole() == Role.CANDIDATE) {
            candidateProfileRepository.save(CandidateProfile.builder().userId(user.getId()).build());
        } else if (user.getRole() == Role.RECRUITER) {
            recruiterProfileRepository.save(RecruiterProfile.builder().userId(user.getId()).build());
        }
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ApiException("Invalid email or password", HttpStatus.UNAUTHORIZED));

        String token = jwtUtil.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return new AuthResponse(token, user.getEmail(), user.getName(), user.getRole().name());
    }
}

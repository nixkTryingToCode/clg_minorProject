package com.recruitplatform.backend.service;

import com.recruitplatform.backend.dto.request.RegisterRequest;
import com.recruitplatform.backend.dto.response.AuthResponse;
import com.recruitplatform.backend.entity.CandidateProfile;
import com.recruitplatform.backend.entity.RecruiterProfile;
import com.recruitplatform.backend.entity.User;
import com.recruitplatform.backend.enums.Role;
import com.recruitplatform.backend.repository.CandidateProfileRepository;
import com.recruitplatform.backend.repository.RecruiterProfileRepository;
import com.recruitplatform.backend.repository.UserRepository;
import com.recruitplatform.backend.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CandidateProfileRepository candidateProfileRepository;
    @Mock
    private RecruiterProfileRepository recruiterProfileRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest request(Role role) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        request.setName("Test User");
        request.setRole(role);
        return request;
    }

    private void stubSaveWithId() {
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(UUID.randomUUID());
            return user;
        });
    }

    @Test
    void registerCandidateCreatesCandidateProfile() {
        stubSaveWithId();
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        AuthResponse response = authService.register(request(Role.CANDIDATE));

        assertThat(response.getRole()).isEqualTo("CANDIDATE");
        ArgumentCaptor<CandidateProfile> captor = ArgumentCaptor.forClass(CandidateProfile.class);
        verify(candidateProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isNotNull();
        verify(recruiterProfileRepository, never()).save(any());
    }

    @Test
    void registerRecruiterCreatesRecruiterProfile() {
        stubSaveWithId();
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        AuthResponse response = authService.register(request(Role.RECRUITER));

        assertThat(response.getRole()).isEqualTo("RECRUITER");
        ArgumentCaptor<RecruiterProfile> captor = ArgumentCaptor.forClass(RecruiterProfile.class);
        verify(recruiterProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isNotNull();
        verify(candidateProfileRepository, never()).save(any());
    }

    @Test
    void registerAdminCreatesNoProfile() {
        stubSaveWithId();
        when(jwtUtil.generateToken(any(), any())).thenReturn("token");

        authService.register(request(Role.ADMIN));

        verify(candidateProfileRepository, never()).save(any());
        verify(recruiterProfileRepository, never()).save(any());
    }
}
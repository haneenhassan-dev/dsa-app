package com.haneen.dsa.service;

import com.haneen.dsa.dto.LoginRequest;
import com.haneen.dsa.model.Role;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.OrganizationRepository;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    // dummies
    @Mock UserRepository userRepository;
    @Mock PasswordService passwordService;
    @Mock JwtUtil jwtUtil;
    @Mock OrganizationRepository organizationRepository;   // login doesn't use it, but AuthService's constructor needs it

    // real AuthService with the dummies plugged in
    @InjectMocks AuthService authService;

    LoginRequest req;

    // runs before EVERY test → each test gets fresh input
    @BeforeEach
    void setUp() {
        req = new LoginRequest();
        req.setUsername("haneen");
        req.setPassword("secret");
    }

    // PATH 3: success
    @Test
    void login_validCredentials_returnsToken() {
        User user = new User();
        user.setUsername("haneen");
        user.setPasswordHash("hashed");
        user.setRole(Role.ADMIN);

        when(userRepository.findByUsernameForLogin("haneen")).thenReturn(Optional.of(user));
        when(passwordService.matches("secret", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any(), any(), any())).thenReturn("fake-jwt");

        assertEquals("fake-jwt", authService.login(req));
    }

    // PATH 1: user not found
    @Test
    void login_unknownUser_throwsAndNeverChecksPassword() {
        when(userRepository.findByUsernameForLogin("haneen")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> authService.login(req));

        // proves login exits early: password is never checked
        verify(passwordService, never()).matches(any(), any());
    }

    // PATH 2: wrong password → YOU write this one
    // hints: user exists, but matches(...) returns false
    //        expect BadCredentialsException
    //        bonus: verify jwtUtil.generateToken is never called
}
package com.haneen.dsa.controller;

import com.haneen.dsa.dto.AuthResponse;
import com.haneen.dsa.dto.LoginRequest;
import com.haneen.dsa.dto.RegisterRequest;
import com.haneen.dsa.model.Organization;
import com.haneen.dsa.model.Role;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.OrganizationRepository;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.security.JwtUtil;
import com.haneen.dsa.service.AuthService;
import com.haneen.dsa.service.PasswordService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;

    public AuthController(UserRepository userRepository,
                          PasswordService passwordService,
                          JwtUtil jwtUtil, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
        this.authService= authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        //authService.registerOrgAndUser(req);
        try{
            authService.registerOrgAndUser(req);
            return ResponseEntity.ok("Registered");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(new AuthResponse(authService.login(req)));
    }
}

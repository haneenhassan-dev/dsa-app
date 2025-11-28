package com.haneen.dsa.controller;

import com.haneen.dsa.dto.AuthResponse;
import com.haneen.dsa.dto.LoginRequest;
import com.haneen.dsa.dto.RegisterRequest;
import com.haneen.dsa.model.Role;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.security.JwtUtil;
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

    public AuthController(UserRepository userRepository,
                          PasswordService passwordService,
                          JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordService = passwordService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body("Username already taken");
        }
        if (userRepository.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email already taken");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        // Hash password before saving
        user.setPasswordHash(passwordService.hash(req.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);
        return ResponseEntity.ok("Registered");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        Optional<User> opt = userRepository.findByUsername(req.getUsername());
        if (opt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        User user = opt.get();
        if (!passwordService.matches(req.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}

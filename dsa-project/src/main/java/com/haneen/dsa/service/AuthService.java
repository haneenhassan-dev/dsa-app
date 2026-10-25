package com.haneen.dsa.service;
import com.haneen.dsa.dto.AuthResponse;
import com.haneen.dsa.dto.LoginRequest;
import com.haneen.dsa.dto.RegisterRequest;
import com.haneen.dsa.exception.DuplicateResourceException;
import com.haneen.dsa.model.Organization;
import com.haneen.dsa.model.Role;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.OrganizationRepository;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.security.JwtUtil;
import com.haneen.dsa.security.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthService {
    // Inject repositories and passwordService here

    @Autowired
    private final OrganizationRepository organizationRepository;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;


    public AuthService(OrganizationRepository organizationRepository, PasswordService passwordService, UserRepository userRepository, JwtUtil jwtUtil) {
        this.organizationRepository = organizationRepository;
        this.passwordService = passwordService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public void registerOrgAndUser(RegisterRequest req) {
        if (userRepository.existsUsernameAllOrgs(req.getUsername())==1) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsEmailAllOrgs(req.getEmail())==1) {
            throw new DuplicateResourceException("Email already taken");
        }
        String orgName = req.getOrgName();
        if (orgName == null || orgName.trim().isEmpty()) {
            orgName = req.getEmail() + "'s Workspace";
        }

        if (organizationRepository.existsByOrgName(orgName)) {
            throw new DuplicateResourceException("Workspace name is already taken!");
        }

        try {
            Organization org = new Organization();
            org.setOrgName(orgName);
            org = organizationRepository.save(org);
            User user = new User();
            user.setTenantOrgId(org.getOrgId());
            user.setUsername(req.getUsername());
            user.setEmail(req.getEmail());
            user.setPasswordHash(passwordService.hash(req.getPassword()));
            user.setRole(Role.ADMIN); // They own this workspace
            userRepository.save(user);
        }
        finally {
            TenantContext.clear();
        }
    }


    public String login(LoginRequest req) {
        Optional<User> opt = userRepository.findByUsernameForLogin(req.getUsername());
        if (opt.isEmpty()) {
            throw new BadCredentialsException("Invalid credentials");
        }

        User user = opt.get();
        if (!passwordService.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRole().name(), user.getTenantOrgId());
        return token;
    }
}
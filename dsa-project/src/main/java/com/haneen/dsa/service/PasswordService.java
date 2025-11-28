package com.haneen.dsa.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // Hash plaintext password (store this in DB)
    public String hash(String raw) {
        return encoder.encode(raw);
    }

    // Verify raw password against stored hash
    public boolean matches(String raw, String hashed) {
        return encoder.matches(raw, hashed);
    }
}

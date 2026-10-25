package com.haneen.dsa.repository;

import com.haneen.dsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);


    @Query(value = "SELECT * FROM users WHERE username = :username", nativeQuery = true)
    Optional<User> findByUsernameForLogin(String username);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE email = :email)", nativeQuery = true)
    long existsEmailAllOrgs(String email);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM users WHERE username = :username)", nativeQuery = true)
    long existsUsernameAllOrgs(String username);
}


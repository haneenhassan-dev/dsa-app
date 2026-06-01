package com.haneen.dsa.repository;

import com.haneen.dsa.model.Draft;
import com.haneen.dsa.model.Problem;
import com.haneen.dsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DraftRepository extends JpaRepository<Draft,Long> {
    Optional<Draft> findByUserAndProblemAndLanguage(User user, Problem problem, String language);

}

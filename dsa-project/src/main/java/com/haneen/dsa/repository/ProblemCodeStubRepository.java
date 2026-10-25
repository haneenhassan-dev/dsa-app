package com.haneen.dsa.repository;

import com.haneen.dsa.model.ProblemCodeStub;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProblemCodeStubRepository extends JpaRepository<ProblemCodeStub, Long> {
    Optional<ProblemCodeStub> findByProblemProblemIdAndLanguage(Long problemId, String language);
}
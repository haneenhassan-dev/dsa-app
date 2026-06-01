package com.haneen.dsa.repository;

import com.haneen.dsa.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission,Long> {
    Optional<List<Submission>> findByUserUserIdAndProblemProblemId (Long userId, Long problemId);
}

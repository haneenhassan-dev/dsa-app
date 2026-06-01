package com.haneen.dsa.repository;

import com.haneen.dsa.model.Problem;
import com.haneen.dsa.model.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestCaseRepository extends JpaRepository<TestCase,Long> {
    public List<TestCase> findByProblem(Problem problem);
}

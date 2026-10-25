package com.haneen.dsa.service;

import com.haneen.dsa.dto.JudgeResult;
import com.haneen.dsa.dto.SubmissionRequest;
import com.haneen.dsa.dto.Verdict;
import com.haneen.dsa.model.*;
import com.haneen.dsa.repository.*;
import com.haneen.dsa.security.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import com.haneen.dsa.model.Submission;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class SubmissionService {

    @Autowired
    private TestCaseRepository testCaseRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private ProblemRepository problemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ProblemCodeStubRepository stubRepository;

    @Autowired
    SubmissionTestCaseRepository submissionTestCaseRepository;

    @Autowired
    JudgeService judgeService;

    @Autowired
    SubmissionFinalizer submissionFinalizer;

    @Autowired
    JudgeRunner judgeRunner;

    public Optional<Submission> getSubmissionById(Long submissionId){
        Optional<Submission> optionalSubmission = submissionRepository.findById(submissionId);
        return optionalSubmission;
    }

    public List<Submission> getSubmissions(Long userId, Long problemId){
        Optional<List<Submission>> optionalSubmissions = submissionRepository.findByUserUserIdAndProblemProblemId(userId, problemId);
        return optionalSubmissions.orElseGet(List::of);
    }
    public Long submitProblem(SubmissionRequest req, Long userId) throws Exception {
        Problem problem = problemRepository.findById(req.getProblemId()).orElseThrow(() -> new RuntimeException("Problem not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        String language = (req.getLanguage() == null) ? "JAVA" : req.getLanguage();
        ProblemCodeStub stub = stubRepository
                .findByProblemProblemIdAndLanguage(problem.getProblemId(), language)
                .orElseThrow(() -> new RuntimeException("No code stub for " + language));

        Submission submission = new Submission();
        submission.setUser(user);
        submission.setProblem(problem);
        submission.setCode(req.getCode());
        submission.setLanguage(language);
        submission.setStatus(SubmissionStatus.RUNNING);
        submissionRepository.save(submission);
        Long submissionId = submission.getSubmissionId();

        List<TestCase> testCases = testCaseRepository.findByProblem(problem);
        Long orgId = TenantContext.getTenantId();               // capture on the REQUEST thread

        judgeRunner.runAsync(submissionId, orgId, req.getCode(), stub.getDriverCode(),testCases);

        return submissionId;
    }


}

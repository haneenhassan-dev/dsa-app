package com.haneen.dsa.service;
import com.haneen.dsa.dto.JudgeResult;
import com.haneen.dsa.dto.Verdict;
import com.haneen.dsa.model.*;
import com.haneen.dsa.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubmissionFinalizer {

    @Autowired
    SubmissionRepository submissionRepository;
    @Autowired
    SubmissionTestCaseRepository submissionTestCaseRepository;

    @Autowired
    TestCaseRepository testCaseRepository;

    @Transactional
    public void markFailed(Long submissionId){
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("submission not found"));

        submission.setStatus(SubmissionStatus.SYSTEM_ERROR);
    }
    @Transactional
    public String finalizeSubmission(Long submissionId, JudgeResult result) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("submission not found"));
        Organization org = submission.getOrganization();

        if (result.getKind() == JudgeResult.Kind.COMPILE_ERROR) {
            submission.setStatus(SubmissionStatus.COMPILATION_ERROR);
            return "COMPILATION_ERROR";
        }
        if (result.getKind() == JudgeResult.Kind.OVERALL_TIMEOUT) {
            submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
            return submission.getStatus().toString();
        }
        List<TestCase> testCases = testCaseRepository.findByProblem(submission.getProblem());
        if (result.getKind() != JudgeResult.Kind.OK) {
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            return submission.getStatus().toString();
        }

        testCases = testCaseRepository.findByProblem(submission.getProblem());
        Map<Long, TestCase> byId = new HashMap<>();
        for (TestCase tc : testCases) byId.put(tc.getTestCaseId(), tc);

        SubmissionStatus overall = SubmissionStatus.ACCEPTED;
        for (Verdict v : result.getVerdicts()) {
            SubmissionTestCase stc = new SubmissionTestCase();
            stc.setSubmission(submission);
            stc.setTestCase(byId.get(v.getTestcaseId()));
            stc.setActualOutput(v.getActualOutput());
            stc.setStatus(TestCaseStatus.valueOf(v.getStatus()));
            stc.setExecTimeMs(v.getExecTimeMs());
            stc.setOrganization(org);
            submissionTestCaseRepository.save(stc);

            if (overall == SubmissionStatus.ACCEPTED && !"AC".equals(v.getStatus())) {
                overall = switch (v.getStatus()) {
                    case "WA"  -> SubmissionStatus.WRONG_ANSWER;
                    case "TLE" -> SubmissionStatus.TIME_LIMIT_EXCEEDED;
                    default    -> SubmissionStatus.RUNTIME_ERROR;
                };
            }
        }
        submission.setStatus(overall);
        return overall.toString();
    }
}

package com.haneen.dsa.service;

import com.haneen.dsa.dto.JudgeResult;
import com.haneen.dsa.dto.Verdict;
import com.haneen.dsa.model.*;
import com.haneen.dsa.repository.SubmissionRepository;
import com.haneen.dsa.repository.SubmissionTestCaseRepository;
import com.haneen.dsa.repository.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubmissionFinalizerTest {

    @Mock
    SubmissionRepository submissionRepository;
    @Mock
    SubmissionTestCaseRepository submissionTestCaseRepository;
    @Mock
    TestCaseRepository testCaseRepository;

    @InjectMocks SubmissionFinalizer submissionFinalizer;

    Long submissionid;
    Submission submission;
    JudgeResult judgeResult;
    @BeforeEach
    void setUp(){
        submission = new Submission();
        submissionid = 1L;
        submission.setSubmissionId(submissionid);
        submission.setStatus(SubmissionStatus.RUNNING);
        Verdict verdict = new Verdict();
        verdict.setActualOutput("1");
        verdict.setExecTimeMs(10);
        verdict.setTestcaseId(1L);
        verdict.setStatus("AC");
        judgeResult = JudgeResult.ok(List.of(verdict));
    }

    @Test
    void submit_with_compilationError(){
        when(submissionRepository.findById(submissionid)).thenReturn(Optional.of(submission));
        judgeResult = JudgeResult.compileError("COMPILATION_ERROR");
        String result = submissionFinalizer.finalizeSubmission(submissionid,judgeResult);
        assertEquals(SubmissionStatus.COMPILATION_ERROR,submission.getStatus());
        verify(submissionTestCaseRepository, never()).save(any());
        verify(testCaseRepository, never()).findByProblem(any());
    }

    @Test
    void submit_with_no_errors(){
        Problem problem = new Problem();
        problem.setProblemId(101L);

        Organization org = new Organization();
        org.setOrgId(7L);
        submission.setOrganization(org);
        submission.setProblem(problem);
        when(submissionRepository.findById(submissionid))
                .thenReturn(Optional.of(submission));

        TestCase tc = new TestCase();
        tc.setTestCaseId(1L);
        tc.setProblem(problem);
        tc.setInput("1");          // or valid JSON according to your test data model
        tc.setExpectedOutput("1");  // same idea
        when(testCaseRepository.findByProblem(problem)).thenReturn(List.of(tc));
        String result = submissionFinalizer.finalizeSubmission(submissionid,judgeResult);
        assertEquals(SubmissionStatus.ACCEPTED,submission.getStatus());
    }

    @Test
    void submit_with_wrong_answer(){

        Problem problem = new Problem();
        problem.setProblemId(101L);

        Verdict verdict1 = new Verdict();
        verdict1.setActualOutput("12");
        verdict1.setExecTimeMs(10);
        verdict1.setTestcaseId(1L);
        verdict1.setStatus("WA");

        Verdict verdict2 = new Verdict();
        verdict2.setActualOutput("");
        verdict2.setExecTimeMs(10);
        verdict2.setTestcaseId(1L);
        verdict2.setStatus("TLE");

        Organization org = new Organization();
        org.setOrgId(7L);
        submission.setOrganization(org);
        submission.setProblem(problem);

        TestCase tc1 = new TestCase();
        tc1.setTestCaseId(1L);

        TestCase tc2 = new TestCase();
        tc2.setTestCaseId(2L);

        judgeResult = JudgeResult.ok(List.of(verdict1,verdict2));
        when(submissionRepository.findById(submissionid)).thenReturn(Optional.of(submission));
        when(testCaseRepository.findByProblem(submission.getProblem())).thenReturn(List.of(tc1,tc2));

        String result = submissionFinalizer.finalizeSubmission(submissionid, judgeResult);


        assertEquals(SubmissionStatus.WRONG_ANSWER,submission.getStatus());


    }
}

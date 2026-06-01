package com.haneen.dsa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "submission_testcases")
public class SubmissionTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    @ManyToOne
    @JoinColumn(name = "testcase_id", nullable = false)
    private TestCase testCase;

    @Column(name = "actual_output", columnDefinition = "TEXT")
    private String actualOutput;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCaseStatus status = TestCaseStatus.PENDING;

    @Column(name = "exec_time_ms")
    private Integer execTimeMs;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Submission getSubmission() { return submission; }
    public void setSubmission(Submission submission) { this.submission = submission; }

    public TestCase getTestCase() { return testCase; }
    public void setTestCase(TestCase testCase) { this.testCase = testCase; }

    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }

    public TestCaseStatus getStatus() { return status; }
    public void setStatus(TestCaseStatus status) { this.status = status; }

    public Integer getExecTimeMs() { return execTimeMs; }
    public void setExecTimeMs(Integer execTimeMs) { this.execTimeMs = execTimeMs; }
}
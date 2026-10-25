package com.haneen.dsa.model;

import jakarta.persistence.*;
import org.hibernate.annotations.TenantId;

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

    @TenantId
    @Column(name = "org_id", nullable = false)
    private Long tenantOrgId;

    @ManyToOne
    @JoinColumn(name="org_id", insertable = false, updatable = false)
    private Organization organization;


    // Getters and Setters

    public Long getTenantOrgId() {
        return tenantOrgId;
    }
    public void setTenantOrgId(Long tenantOrgId) {
        this.tenantOrgId = tenantOrgId;
    }
    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

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
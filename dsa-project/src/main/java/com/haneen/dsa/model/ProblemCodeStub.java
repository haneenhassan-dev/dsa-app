package com.haneen.dsa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "problem_code_stub")
public class ProblemCodeStub {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stub_id")
    private Long stubId;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(nullable = false)
    private String language;

    @Column(name = "function_name", nullable = false)
    private String functionName;

    @Column(name = "starter_code", nullable = false, columnDefinition = "TEXT")
    private String starterCode;

    @Column(name = "driver_code", nullable = false, columnDefinition = "TEXT")
    private String driverCode;

    public Long getStubId() { return stubId; }
    public Problem getProblem() { return problem; }
    public void setProblem(Problem problem) { this.problem = problem; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getFunctionName() { return functionName; }
    public void setFunctionName(String functionName) { this.functionName = functionName; }
    public String getStarterCode() { return starterCode; }
    public void setStarterCode(String starterCode) { this.starterCode = starterCode; }
    public String getDriverCode() { return driverCode; }
    public void setDriverCode(String driverCode) { this.driverCode = driverCode; }
}
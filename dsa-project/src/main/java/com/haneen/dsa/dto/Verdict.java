package com.haneen.dsa.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Verdict {
    private Long testcaseId;
    private String status;        // "AC" | "WA" | "TLE" | "RE"
    private String actualOutput;  // null for TLE
    private Integer execTimeMs;   // null for TLE/RE

    public Long getTestcaseId() { return testcaseId; }
    public void setTestcaseId(Long testcaseId) { this.testcaseId = testcaseId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getActualOutput() { return actualOutput; }
    public void setActualOutput(String actualOutput) { this.actualOutput = actualOutput; }
    public Integer getExecTimeMs() { return execTimeMs; }
    public void setExecTimeMs(Integer execTimeMs) { this.execTimeMs = execTimeMs; }
}
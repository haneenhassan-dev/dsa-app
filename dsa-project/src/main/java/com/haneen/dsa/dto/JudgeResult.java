package com.haneen.dsa.dto;

import com.haneen.dsa.dto.Verdict;
import java.util.List;

public class JudgeResult {
    public enum Kind { OK, COMPILE_ERROR, OVERALL_TIMEOUT, INTERNAL_ERROR }
    private final Kind kind;
    private final List<Verdict> verdicts;
    private final String message;

    private JudgeResult(Kind kind, List<Verdict> verdicts, String message) {
        this.kind = kind; this.verdicts = verdicts; this.message = message;
    }
    public static JudgeResult ok(List<Verdict> v) { return new JudgeResult(Kind.OK, v, null); }
    public static JudgeResult compileError(String m) { return new JudgeResult(Kind.COMPILE_ERROR, null, m); }
    public static JudgeResult overallTimeout() { return new JudgeResult(Kind.OVERALL_TIMEOUT, null, null); }
    public static JudgeResult internalError(String m) { return new JudgeResult(Kind.INTERNAL_ERROR, null, m); }

    public Kind getKind() { return kind; }
    public List<Verdict> getVerdicts() { return verdicts; }
    public String getMessage() { return message; }
}
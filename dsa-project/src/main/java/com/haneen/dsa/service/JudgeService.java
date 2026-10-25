package com.haneen.dsa.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.haneen.dsa.dto.JudgeResult;
import com.haneen.dsa.dto.Verdict;
import com.haneen.dsa.model.TestCase;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {

    private final ObjectMapper mapper = new ObjectMapper();

    private String jacksonClasspath() throws Exception {
        String sep = File.pathSeparator;
        return jarOf(com.fasterxml.jackson.databind.ObjectMapper.class) + sep
                + jarOf(com.fasterxml.jackson.core.JsonFactory.class) + sep
                + jarOf(com.fasterxml.jackson.annotation.JsonIgnoreProperties.class);
    }

    private String jarOf(Class<?> c) throws Exception {
        return new File(c.getProtectionDomain().getCodeSource()
                .getLocation().toURI()).getAbsolutePath();
    }
    public JudgeResult judge(String userCode, String driverCode, List<TestCase> testCases)
            throws Exception {
        Path base = Path.of(System.getProperty("user.home"),"judge-work");
        Files.createDirectories(base);
        Path dir = Files.createTempDirectory(base,"submission_");
        try {
            // 1. write sources
            Files.writeString(dir.resolve("Solution.java"), userCode);
            Files.writeString(dir.resolve("Main.java"), driverCode);

            // 2. build cases.json (input/expected embedded as real JSON, not strings)
            ArrayNode cases = mapper.createArrayNode();
            for (TestCase tc : testCases) {
                ObjectNode n = mapper.createObjectNode();
                n.put("testcaseId", tc.getTestCaseId());
                n.set("input", mapper.readTree(tc.getInput()));
                n.set("expected", mapper.readTree(tc.getExpectedOutput()));
                cases.add(n);
            }
            Files.writeString(dir.resolve("cases.json"), mapper.writeValueAsString(cases));

            /*
            // 3. compile Main + Solution together (stderr → file, with timeout)
//            docker run --rm -v "$PWD":/work dsa-judge:latest \
//            sh -c "javac -cp '/opt/judge/libs/*' Main.java Solution.java && java -cp '.:/opt/judge/libs/*' Main cases.json results.json"
            String cp = jacksonClasspath();
            Process compile = new ProcessBuilder("javac", "-cp", cp, "Main.java", "Solution.java")
                    .directory(dir.toFile())
                    .redirectError(dir.resolve("compile_err.log").toFile())
                    .start();
            if (!compile.waitFor(10, TimeUnit.SECONDS)) {
                compile.destroyForcibly();
                return JudgeResult.compileError("Compilation timed out");
            }
            if (compile.exitValue() != 0) {
                return JudgeResult.compileError(Files.readString(dir.resolve("compile_err.log")));
            }

            // 4. run driver ONCE (user stdout/stderr → files, so no pipe deadlock)
            String runCp = "." + File.pathSeparator + cp;
            Process run = new ProcessBuilder("java", "-cp", runCp, "Main", "cases.json", "results.json")
                    .directory(dir.toFile())
                    .redirectOutput(dir.resolve("stdout.log").toFile())
                    .redirectError(dir.resolve("stderr.log").toFile())
                    .start();
            if (!run.waitFor(30, TimeUnit.SECONDS)) {   // overall safety net
                run.destroyForcibly();
                return JudgeResult.overallTimeout();
            }

             */

            Process mainRun = new ProcessBuilder(
                    "doccker", "run", "--rm",
                    "--network", "none",
                    "--memory", "256m",
                    "--cpus", "1",
                    "--pids-limit", "64",
                    "-v", dir.toAbsolutePath() + ":/work",
                    "dsa-judge:latest",
                    "sh", "-c",
                    "javac -cp '/opt/judge/libs/*' Main.java Solution.java 2> compile_err.log; " +
                            "if [ $? -ne 0 ]; then exit 2; fi; " +
                            "java -cp '.:/opt/judge/libs/*' Main cases.json results.json"
            )
                    .directory(dir.toFile())
                    .redirectOutput(dir.resolve("stdout.log").toFile())
                    .redirectError(dir.resolve("stderr.log").toFile())
                    .start();

            System.out.println(dir.toAbsolutePath());
            if (!mainRun.waitFor(30, TimeUnit.SECONDS)) {   // overall safety net
                mainRun.destroyForcibly();
                return JudgeResult.overallTimeout();
            }
            // 5. read verdicts (guard against driver crashing before writing)
            Path results = dir.resolve("results.json");
            if (mainRun.exitValue()==2) {
                System.out.println("exit 2");
                return JudgeResult.compileError(Files.readString(dir.resolve("compile_err.log")));
            }

            System.out.println(mainRun.exitValue());
            if(mainRun.exitValue()!=0 || !Files.exists(results)){
                System.out.println("exit other");
                return JudgeResult.internalError(Files.readString(dir.resolve("stderr.log")));
            }
            List<Verdict> verdicts =
                    Arrays.asList(mapper.readValue(Files.readString(results), Verdict[].class));
            return JudgeResult.ok(verdicts);

        } finally {
            try (var walk = Files.walk(dir)) {
                walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        }
    }
}
package com.haneen.dsa.service;

import com.haneen.dsa.dto.SubmissionRequest;
import com.haneen.dsa.model.*;
import com.haneen.dsa.repository.ProblemRepository;
import com.haneen.dsa.repository.SubmissionRepository;
import com.haneen.dsa.repository.TestCaseRepository;
import com.haneen.dsa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    public List<Submission> getSubmissions(Long userId, Long problemId){
        Optional<List<Submission>> optionalSubmissions = submissionRepository.findByUserUserIdAndProblemProblemId(userId, problemId);
        return optionalSubmissions.orElseGet(List::of);
    }
    public String submitProblem(SubmissionRequest req, Long userId) throws IOException,InterruptedException {
        String result ="";
        Problem problem = problemRepository.findById(req.getProblemId()).orElseThrow(()-> new RuntimeException("Problem not found"));
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));

        Submission submission = new Submission();
        submission.setCode(req.getCode());
        submission.setProblem(problem);
        submission.setLanguage(req.getLanguage());
        submission.setStatus(SubmissionStatus.PENDING);
        submission.setUser(user);
        submissionRepository.save(submission);

        Path tempDir = Files.createTempDirectory("submission_");
        Path codeFile = tempDir.resolve("Solution.java");
        Files.writeString(codeFile,req.getCode());

        ProcessBuilder compile = new ProcessBuilder("javac", "Solution.java");
        compile.directory(tempDir.toFile());
        Process compileProcess = compile.start();
        int exitCode = compileProcess.waitFor();
        if (exitCode != 0) {
            // compilation failed — read the error
            String error = new String(compileProcess.getErrorStream().readAllBytes());
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> path.toFile().delete());
            
            return "COMPILATION_ERROR";
        }

        List<TestCase> testCases = testCaseRepository.findByProblem(problem);
        boolean pass=true;
 
         try{
            for(TestCase testCase : testCases){
                ProcessBuilder run = new ProcessBuilder("java", "Solution");
                run.directory(tempDir.toFile());
                Process runProcess = run.start();
                runProcess.getOutputStream().write(testCase.getInput().getBytes());
                runProcess.getOutputStream().close();

                boolean finished = runProcess.waitFor(5, TimeUnit.SECONDS);
                if(!finished){
                    runProcess.destroyForcibly();
                    submission.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    submissionRepository.save(submission);
                    return SubmissionStatus.TIME_LIMIT_EXCEEDED.toString();
                }

                String output = new String(runProcess.getInputStream().readAllBytes()).trim();

                if(!output.equals(testCase.getExpectedOutput().trim())){
                    System.out.println(output);
                    System.out.println(testCase.getExpectedOutput().trim());
                    pass=false;
                    break;
                }
                System.out.println(output);
                System.out.println(testCase.getExpectedOutput().trim());

            }
        }
        catch (Exception e) {
            submission.setStatus(SubmissionStatus.RUNTIME_ERROR);
            submissionRepository.save(submission);
            return SubmissionStatus.RUNTIME_ERROR.toString();
        }
        finally {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> path.toFile().delete());
        }

        if (pass) {
            submission.setStatus(SubmissionStatus.ACCEPTED);
        } else {
            submission.setStatus(SubmissionStatus.WRONG_ANSWER);
        }
        submissionRepository.save(submission);
        return pass? SubmissionStatus.ACCEPTED.toString():SubmissionStatus.WRONG_ANSWER.toString();
    }
}

package com.haneen.dsa.controller;

import com.haneen.dsa.dto.SubmissionRequest;
import com.haneen.dsa.model.Submission;
import com.haneen.dsa.principal.UserPrincipal;
import com.haneen.dsa.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionService submissionService;

    @GetMapping
    public ResponseEntity<?> getSubmission(@RequestParam Long problemId){
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUserId();
        List<Submission> submissionList = submissionService.getSubmissions(userId, problemId);
        return ResponseEntity.ok(submissionList);
    }

    @PostMapping
    public ResponseEntity<?> submit(@RequestBody SubmissionRequest req) throws IOException, InterruptedException {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUserId();
        String result = submissionService.submitProblem(req,userId);
        return ResponseEntity.ok(Map.of("status",result));
    }
}

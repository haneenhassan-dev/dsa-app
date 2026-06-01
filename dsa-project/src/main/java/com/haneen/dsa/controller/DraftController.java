package com.haneen.dsa.controller;

import com.haneen.dsa.dto.DraftRequest;
import com.haneen.dsa.model.Draft;
import com.haneen.dsa.model.Problem;
import com.haneen.dsa.model.User;
import com.haneen.dsa.principal.UserPrincipal;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.service.DraftService;
import com.haneen.dsa.service.ProblemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/draft")
public class DraftController {
    @Autowired
    DraftService draftService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProblemService problemService;


    @GetMapping
    public ResponseEntity<?> getDraft(@RequestParam Long problemId, @RequestParam String language){
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        Long userId = principal.getUserId();
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
        Problem problem = problemService.getProblemById(problemId);



        Optional<Draft> draft = draftService.getDraft(user, problem, language);

        if(draft.isPresent()){
            System.out.println("Raw LocalDateTime: " + draft.get().getUpdatedAt());
            System.out.println("Epoch millis: " + draft.get().getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            System.out.println("Current millis: " + System.currentTimeMillis());
            return ResponseEntity.ok(Map.of(
                    "code",draft.get().getCode(),
                    "updatedAt",draft.get().getUpdatedAt().atZone(ZoneId.systemDefault()).toInstant().toString()
            ));
        }
        else{
            return ResponseEntity.ok(Map.of("code","No draft found"));
        }
    }

    @PostMapping
    public ResponseEntity<?> addDraft(@RequestBody DraftRequest draftRequest){
        try{
            UserPrincipal principal = (UserPrincipal) SecurityContextHolder
                    .getContext().getAuthentication().getPrincipal();
            Long userId = principal.getUserId();
            User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User not found"));
            Problem problem = problemService.getProblemById(draftRequest.getProblemId());
            String code = draftRequest.getCode();
            if(draftService.saveDraft(user,problem, draftRequest.getLanguage(), code)){
                return ResponseEntity.ok(Map.of("Message","Draft saved successfully!"));
            }
            else{
                return ResponseEntity.ok(Map.of("Message","Failed to update draft"));
            }
        }

        catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }

    }

}

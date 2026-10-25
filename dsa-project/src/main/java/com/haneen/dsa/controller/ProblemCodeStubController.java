package com.haneen.dsa.controller;


import com.haneen.dsa.model.ProblemCodeStub;
import com.haneen.dsa.repository.ProblemCodeStubRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
public class ProblemCodeStubController {

    ProblemCodeStubRepository problemCodeStubRepository;


    ProblemCodeStubController(ProblemCodeStubRepository problemCodeStubRepository){
        this.problemCodeStubRepository = problemCodeStubRepository;
    }

    @GetMapping("/problems/{problemId}/stub")
    public ResponseEntity<?> getStarterCode(@PathVariable Long problemId, @RequestParam String language){
        Optional<ProblemCodeStub> optionalProblemCodeStub= problemCodeStubRepository.findByProblemProblemIdAndLanguage(problemId,language);
        return optionalProblemCodeStub.map(stub->ResponseEntity.ok(Map.of("starter code",stub.getStarterCode())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

package com.haneen.dsa.service;


import com.haneen.dsa.dto.JudgeResult;
import com.haneen.dsa.model.Submission;
import com.haneen.dsa.model.TestCase;
import com.haneen.dsa.security.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JudgeRunner {
    private static final Logger log = LoggerFactory.getLogger(JudgeRunner.class);

    @Autowired
    JudgeService judgeService;
    @Autowired private SubmissionFinalizer submissionFinalizer;

    @Async("judgeExecutor")
    public void runAsync(Long submissionId, Long orgId, String code, String driverCode, List<TestCase> testCases){
        try{
            log.info("Submission {} queued for org {}", submissionId, orgId);
            TenantContext.setTenantId(orgId);
            JudgeResult judgeResult = judgeService.judge(code,driverCode,testCases);
            submissionFinalizer.finalizeSubmission(submissionId,judgeResult);
        }
        catch (Exception e){
            //e.printStackTrace();
            log.error("submission judging failed for {}",submissionId,e);
            try{
                submissionFinalizer.markFailed(submissionId);
            }
            catch (Exception markEx){
                log.error("markFailed also failed; submission {} stuck in RUNNING", submissionId, markEx);
            }

        }
        finally {
            TenantContext.clear();
        }
    }
}

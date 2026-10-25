package com.haneen.dsa.service;

import com.haneen.dsa.model.Draft;
import com.haneen.dsa.model.Problem;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.DraftRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DraftService {
    @Autowired
    DraftRepository draftRepository;

    public Optional<Draft> getDraft(User user, Problem problem, String language){
        return draftRepository.findByUserAndProblemAndLanguage(user,problem,language);
    }

//    public boolean saveDraft(User user, Problem problem, String language, String code){
//        Optional<Draft> draftOptional = draftRepository.findByUserAndProblemAndLanguage(user,problem,language);
//        if(draftOptional.isPresent()){
//            Draft draft= draftOptional.get();
//            draft.setCode(code);
//            draftRepository.save(draft);
//            return true;
//        }
//        else{
//            Draft draft = new Draft();
//            draft.setLanguage(language);
//            draft.setCode(code);
//            draft.setProblem(problem);
//            draft.setUser(user);
//            draft.setOrganization(user.getOrganization());
//            try{
//                draftRepository.save(draft);
//            } catch (DataIntegrityViolationException e) {
//                draftOptional = draftRepository.findByUserAndProblemAndLanguage(user,problem,language);
//                if(draftOptional.isPresent()){
//                    draft = draftOptional.get();
//                    draftRepository.save(draft);
//                }
//            }
//
//            return true;
//        }
//    }

    @Transactional
    public boolean saveDraft(User user, Problem problem, String language, String code){
        draftRepository.upsertDraft(
                user.getUserId(),
                problem.getProblemId(),
                language,
                code,
                user.getTenantOrgId()   // org from the loaded user
        );
        return true;
    }
}

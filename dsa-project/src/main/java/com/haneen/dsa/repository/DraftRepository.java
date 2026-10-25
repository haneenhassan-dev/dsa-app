package com.haneen.dsa.repository;

import com.haneen.dsa.model.Draft;
import com.haneen.dsa.model.Problem;
import com.haneen.dsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DraftRepository extends JpaRepository<Draft,Long> {
    Optional<Draft> findByUserAndProblemAndLanguage(User user, Problem problem, String language);


    @Modifying
    @Query(value = "INSERT INTO drafts (user_id, problem_id, language, code, org_id) " +
            "VALUES (:userId, :problemId, :language, :code, :orgId) " +
            "ON DUPLICATE KEY UPDATE code = VALUES(code)", nativeQuery = true)
    void upsertDraft(@Param("userId") Long userId,
                     @Param("problemId") Long problemId,
                     @Param("language") String language,
                     @Param("code") String code,
                     @Param("orgId") Long orgId);
}

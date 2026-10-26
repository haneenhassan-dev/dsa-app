package com.haneen.dsa.service;

import com.haneen.dsa.dto.MemberResponse;
import com.haneen.dsa.exception.DuplicateResourceException;
import com.haneen.dsa.model.Organization;
import com.haneen.dsa.model.Role;
import com.haneen.dsa.model.User;
import com.haneen.dsa.repository.OrganizationRepository;
import com.haneen.dsa.repository.UserRepository;
import com.haneen.dsa.security.JwtUtil;
import com.haneen.dsa.security.TenantContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrgMemberService {

    UserRepository userRepository;
    PasswordService passwordService;


    public OrgMemberService(PasswordService passwordService, UserRepository userRepository) {
        this.passwordService = passwordService;
        this.userRepository = userRepository;
    }

    @Transactional
    public MemberResponse registerOrgMember(String username, String password, String email){
        if (userRepository.existsUsernameAllOrgs(username)==1) {
            throw new DuplicateResourceException("Username already taken");
        }
        if (userRepository.existsEmailAllOrgs(email)==1) {
            throw new DuplicateResourceException("Email already taken");
        }


        User user = new User();
        //user.setTenantOrgId(TenantContext.getTenantId());
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordService.hash(password));
        user.setRole(Role.USER); // They own this workspace
        userRepository.save(user);
        MemberResponse memberResponse = new MemberResponse(user.getUserId(),user.getUsername(),user.getEmail(),user.getRole().toString());
        return memberResponse;
    }
}

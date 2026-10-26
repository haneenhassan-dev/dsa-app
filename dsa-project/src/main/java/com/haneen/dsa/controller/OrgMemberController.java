package com.haneen.dsa.controller;


import com.haneen.dsa.dto.AddMemberRequest;
import com.haneen.dsa.dto.MemberResponse;
import com.haneen.dsa.model.User;
import com.haneen.dsa.service.OrgMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrgMemberController {

    @Autowired
    OrgMemberService orgMemberService;

    @PostMapping("/org/members")
    public ResponseEntity<?> addMember(@RequestBody AddMemberRequest req){
        MemberResponse memberResponse = orgMemberService.registerOrgMember(req.username(), req.password(), req.email());
        return ResponseEntity.ok(memberResponse);
    }
}

package com.dongnering.member.api;

import com.dongnering.oauth2.google.application.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.dongnering.member.api.dto.request.MemberUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final GoogleOAuthService googleOAuthService;

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestParam String code) {
        String jwtToken = googleOAuthService.loginOrSignUp(googleOAuthService.getGoogleAccessToken(code));
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }

    private final MemberService memberService;

    // 프로필 조회
    @GetMapping("/info")
    public MemberInfoResDto getMemberInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return memberService.getMemberInfo(userDetails.getMemberId());
    }

    // 프로필 수정
    @PutMapping("/info")
    public void updateMemberInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody MemberUpdateReqDto request) {
        memberService.updateMemberInfo(userDetails.getMemberId(), request);
    }



}


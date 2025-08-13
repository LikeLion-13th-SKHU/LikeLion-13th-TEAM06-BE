package com.dongnering.mypage.api;

import com.dongnering.oauth2.google.application.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}


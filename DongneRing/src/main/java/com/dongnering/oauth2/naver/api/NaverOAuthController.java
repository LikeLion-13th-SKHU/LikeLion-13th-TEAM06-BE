package com.dongnering.oauth2.naver.api;

import com.dongnering.oauth2.naver.application.NaverOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class NaverOAuthController {

    private final NaverOAuthService naverOAuthService;

    @GetMapping("/naver")
    public ResponseEntity<?> naverCallback(@RequestParam String code, @RequestParam String state) {
        String jwtToken = naverOAuthService.loginOrSignUp(
                naverOAuthService.getNaverAccessToken(code, state));
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}

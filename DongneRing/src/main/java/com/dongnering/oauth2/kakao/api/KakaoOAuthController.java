package com.dongnering.oauth2.kakao.api;

import com.dongnering.oauth2.kakao.application.KakaoOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping
    public ResponseEntity<?> kakaoCallback(@RequestParam String code) {
        String jwtToken = kakaoOAuthService.loginOrSignUp(kakaoOAuthService.getKakaoAccessToken(code));
        return ResponseEntity.ok(Map.of("token", jwtToken));
    }
}

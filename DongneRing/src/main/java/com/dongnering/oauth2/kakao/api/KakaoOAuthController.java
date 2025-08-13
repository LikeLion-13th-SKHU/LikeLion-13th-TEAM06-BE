package com.dongnering.oauth2.kakao.api;

import com.dongnering.oauth2.kakao.application.KakaoOAuthService;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.common.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login/kakao")
@RequiredArgsConstructor
public class KakaoOAuthController {

    private final KakaoOAuthService kakaoOAuthService;

    @GetMapping
    public ResponseEntity<ApiResTemplate<String>> kakaoCallback(@RequestParam String code) {
        String jwtToken = kakaoOAuthService.loginOrSignUp(
                kakaoOAuthService.getKakaoAccessToken(code));
        return ResponseEntity.ok(ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, jwtToken));
    }
}

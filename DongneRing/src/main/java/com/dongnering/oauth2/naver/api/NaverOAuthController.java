package com.dongnering.oauth2.naver.api;

import com.dongnering.oauth2.naver.application.NaverOAuthService;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.common.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login/naver")
@RequiredArgsConstructor
public class NaverOAuthController {

    private final NaverOAuthService naverOAuthService;

    @GetMapping
    public ResponseEntity<ApiResTemplate<String>> naverCallback(
            @RequestParam String code, @RequestParam String state) {
        String jwtToken = naverOAuthService.loginOrSignUp(
                naverOAuthService.getNaverAccessToken(code, state));
        return ResponseEntity.ok(ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, jwtToken));
    }
}

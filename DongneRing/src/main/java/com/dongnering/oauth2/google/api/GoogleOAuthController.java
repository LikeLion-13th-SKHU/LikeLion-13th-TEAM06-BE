package com.dongnering.oauth2.google.api;

import com.dongnering.oauth2.google.application.GoogleOAuthService;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.common.error.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login/google")
@RequiredArgsConstructor
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping
    public ResponseEntity<ApiResTemplate<String>> googleLogin(@RequestParam String code) {
        String accessToken = googleOAuthService.getGoogleAccessToken(code);
        String jwtToken = googleOAuthService.loginOrSignUp(accessToken);
        return ResponseEntity.ok(ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, jwtToken));
    }
}

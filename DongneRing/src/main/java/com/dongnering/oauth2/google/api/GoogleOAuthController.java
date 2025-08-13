package com.dongnering.oauth2.google.api;

import com.dongnering.oauth2.google.application.GoogleOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/login")
public class GoogleOAuthController {

    private final GoogleOAuthService googleOAuthService;

    @GetMapping("/google")
    public String googleLogin(@RequestParam String code) {
        String accessToken = googleOAuthService.getGoogleAccessToken(code);
        return googleOAuthService.loginOrSignUp(accessToken);
    }
}



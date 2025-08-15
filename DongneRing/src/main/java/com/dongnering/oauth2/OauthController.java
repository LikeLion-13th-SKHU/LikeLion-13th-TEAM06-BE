package com.dongnering.oauth2;

import com.dongnering.common.error.ErrorCode;
import com.dongnering.common.exception.BusinessException;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.global.jwt.TokenResDto;
import com.dongnering.member.domain.Member;
import com.dongnering.oauth2.google.application.GoogleOAuthService;
import com.dongnering.oauth2.kakao.application.KakaoOAuthService;
import com.dongnering.oauth2.naver.application.NaverOAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/login")
public class OauthController {

    private final NaverOAuthService naverOAuthService;
    private final GoogleOAuthService googleOAuthService;
    private final KakaoOAuthService kakaoOAuthService;
    private final OauthService oauthService;

    @Operation(summary = "네이버 로그인 콜백 api", description = "네이버 로그인 콜백 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/naver")
    public ResponseEntity<ApiResTemplate<TokenResDto>> naverCallback(
            @RequestParam String code, @RequestParam String state) {
        Member member = naverOAuthService.processLogin(code, state);
        return oauthService.loginSuccess(member);
    }

    @Operation(summary = "구글 로그인 콜백 api", description = "구글 로그인 콜백 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/google")
    public ResponseEntity<ApiResTemplate<TokenResDto>> googleLogin(@RequestParam String code) {
        Member member = googleOAuthService.processLogin(code);
        return oauthService.loginSuccess(member);
    }

    @Operation(summary = "카카오 로그인 콜백 api", description = "카카오 로그인 콜백 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/kakao")
    public ResponseEntity<ApiResTemplate<TokenResDto>> kakaoCallback(@RequestParam String code) {
        Member member = kakaoOAuthService.processLogin(code);
        return oauthService.loginSuccess(member);
    }

    @Operation(summary = "리프레시 토큰으로 액세스 토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급에 성공하였습니다."),
            @ApiResponse(responseCode = "401", description = "리프레시 토큰 없음 또는 만료")
    })
    @PostMapping("/refreshtoken")
    public ResponseEntity<ApiResTemplate<String>> refreshAccessToken(String refreshToken) {
        if (refreshToken == null) {
            throw new BusinessException(ErrorCode.NO_AUTHORIZATION_EXCEPTION, "리프레시 토큰이 없습니다.");
        }
        return oauthService.reissueAccessToken(refreshToken);
    }
}

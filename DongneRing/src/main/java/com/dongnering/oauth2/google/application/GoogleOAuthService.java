package com.dongnering.oauth2.google.application;

import com.google.gson.Gson;
import com.dongnering.mypage.domain.Role;
import com.dongnering.mypage.domain.Member;
import com.dongnering.mypage.domain.repository.MemberRepository;
import com.dongnering.oauth2.google.api.dto.GoogleTokenResponse;
import com.dongnering.oauth2.google.api.dto.GoogleUserInfo;
import com.dongnering.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleOAuthService {

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${google.token-uri}")
    private String GOOGLE_TOKEN_URL;

    @Value("${google.user-info-uri}")
    private String GOOGLE_USERINFO_URL;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

//    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
//    private final String GOOGLE_USERINFO_URL = "https://www.googleapis.com/oauth2/v2/userinfo";

    // 구글에서 access token 발급 받기
    public String getGoogleAccessToken(String code) {
        log.info("구글 AccessToken 요청, 받은 code = {}", code);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", GOOGLE_CLIENT_ID);
        body.add("client_secret", GOOGLE_CLIENT_SECRET);
        body.add("redirect_uri", GOOGLE_REDIRECT_URI);
        body.add("grant_type", "authorization_code");

        log.info("토큰 요청 바디: {}", body);
        log.info("토큰 요청 헤더: {}", headers);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(GOOGLE_TOKEN_URL, request, String.class);

        log.info("구글 토큰 응답 상태: {}", response.getStatusCode());
        log.info("구글 토큰 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            GoogleTokenResponse tokenResponse = new Gson().fromJson(response.getBody(), GoogleTokenResponse.class);
            log.info("파싱된 AccessToken: {}", tokenResponse.getAccessToken());
            return tokenResponse.getAccessToken();
        }
        throw new RuntimeException("구글 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    // 구글 access token 으로 사용자 정보 가져오기
    private GoogleUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(GOOGLE_USERINFO_URL, HttpMethod.GET, entity, String.class);

        log.info("구글 사용자정보 응답 상태: {}", response.getStatusCode());
        log.info("구글 사용자정보 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), GoogleUserInfo.class);
        }
        throw new RuntimeException("구글 사용자 정보를 가져오는데 실패했습니다.");
    }


    // 로그인 또는 회원가입 후 JWT 토큰 생성 및 반환
    public String loginOrSignUp(String googleAccessToken) {
        GoogleUserInfo userInfo = getUserInfo(googleAccessToken);

        if (userInfo.getVerifiedEmail() == null || !userInfo.getVerifiedEmail()) {
            throw new RuntimeException("이메일 인증이 되지 않은 유저입니다.");
        }

        Member member = memberRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(userInfo.getEmail())
                        .nickname(userInfo.getName())
                        .pictureUrl(userInfo.getPictureUrl())
                        .role(Role.ROLE_USER)
                        .provider(Member.Provider.GOOGLE)  // 구글 소셜 로그인 표시
                        .build()));

        return jwtTokenProvider.generateToken(member);
    }

    public String loginWithCode(String code) {
        // code로 access token 받기
        String accessToken = getGoogleAccessToken(code);

        // access token으로 로그인 또는 회원가입 후 JWT 발급
        return loginOrSignUp(accessToken);
    }

}

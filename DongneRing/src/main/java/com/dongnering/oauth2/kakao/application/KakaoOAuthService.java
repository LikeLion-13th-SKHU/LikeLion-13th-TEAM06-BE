package com.dongnering.oauth2.kakao.application;

import com.dongnering.global.jwt.JwtTokenProvider;
import com.dongnering.mypage.domain.Member;
import com.dongnering.mypage.domain.Role;
import com.dongnering.mypage.domain.repository.MemberRepository;
import com.dongnering.oauth2.kakao.api.dto.KakaoTokenResponse;
import com.dongnering.oauth2.kakao.api.dto.KakaoUserInfo;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoOAuthService {

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${kakao.token-uri}")
    private String KAKAO_TOKEN_URL;

    @Value("${kakao.user-info-uri}")
    private String KAKAO_USERINFO_URL;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 1) 인가코드로 access token 요청
    public String getKakaoAccessToken(String code) {
        log.info("카카오 AccessToken 요청, 받은 code = {}", code);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("code", code);
        if (KAKAO_CLIENT_SECRET != null && !KAKAO_CLIENT_SECRET.isBlank()) {
            body.add("client_secret", KAKAO_CLIENT_SECRET);
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);

        log.info("카카오 토큰 응답 상태: {}", response.getStatusCode());
        log.info("카카오 토큰 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            KakaoTokenResponse tokenResponse = new Gson().fromJson(response.getBody(), KakaoTokenResponse.class);
            return tokenResponse.getAccessToken();
        }
        throw new RuntimeException("카카오 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    // 2) access token으로 사용자 정보 조회
    private KakaoUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_USERINFO_URL, HttpMethod.GET, entity, String.class
        );

        log.info("카카오 사용자정보 응답 상태: {}", response.getStatusCode());
        log.info("카카오 사용자정보 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), KakaoUserInfo.class);
        }
        throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
    }

    // 3) 로그인/회원가입 후 JWT 발급
    public String loginOrSignUp(String accessToken) {
        KakaoUserInfo userInfo = getUserInfo(accessToken);

        if (userInfo.getKakaoAccount() == null || userInfo.getKakaoAccount().getEmail() == null) {
            throw new RuntimeException("이메일 정보가 없는 유저입니다. 카카오 개발자 콘솔에서 account_email 동의 항목을 '필수'로 설정했는지 확인하세요.");
        }

        String email = userInfo.getKakaoAccount().getEmail();
        String name = userInfo.getKakaoAccount().getProfile() != null
                ? userInfo.getKakaoAccount().getProfile().getNickname()
                : "카카오사용자";
        String picture = userInfo.getKakaoAccount().getProfile() != null
                ? userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
                : null;

        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .nickname(name)
                        .pictureUrl(picture)
                        .role(Role.ROLE_USER)
                        .provider(Member.Provider.KAKAO) // ⚠️ enum에 KAKAO 추가 필요
                        .build()));

        return jwtTokenProvider.generateToken(member);
    }

    // 4) 코드만 받아 전체 로그인 처리
    public String loginWithCode(String code) {
        String accessToken = getKakaoAccessToken(code);
        return loginOrSignUp(accessToken);
    }
}

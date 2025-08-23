package com.dongnering.oauth2.naver.application;

import com.dongnering.oauth2.naver.api.dto.NaverTokenResponse;
import com.dongnering.oauth2.naver.api.dto.NaverUserInfo;
import com.google.gson.Gson;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.Role;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthService {

    @Value("${naver.client-id}")
    private String NAVER_CLIENT_ID;

    @Value("${naver.client-secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${naver.redirect-uri}")
    private String NAVER_REDIRECT_URI;

    @Value("${naver.token-uri}")
    private String NAVER_TOKEN_URL;

    @Value("${naver.user-info-uri}")
    private String NAVER_USERINFO_URL;

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 1) 인가코드로 access token 요청
    public String getNaverAccessToken(String code, String state) {
        log.info("네이버 AccessToken 요청, 받은 code = {}, state = {}", code, state);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", NAVER_CLIENT_ID);
        body.add("client_secret", NAVER_CLIENT_SECRET);
        body.add("code", code);
        body.add("state", state);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(NAVER_TOKEN_URL, request, String.class);

        log.info("네이버 토큰 응답 상태: {}", response.getStatusCode());
        log.info("네이버 토큰 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            // 레코드 DTO 사용
            NaverTokenResponse tokenResponse = new Gson().fromJson(response.getBody(), NaverTokenResponse.class);
            return tokenResponse.accessToken();
        }
        throw new RuntimeException("네이버 엑세스 토큰을 가져오는데 실패했습니다.");
    }

    // 2) access token으로 사용자 정보 조회
    private NaverUserInfo getUserInfo(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(NAVER_USERINFO_URL, HttpMethod.GET, entity, String.class);

        log.info("네이버 사용자정보 응답 상태: {}", response.getStatusCode());
        log.info("네이버 사용자정보 응답 바디: {}", response.getBody());

        if (response.getStatusCode() == HttpStatus.OK) {
            return new Gson().fromJson(response.getBody(), NaverUserInfo.class);
        }
        throw new RuntimeException("네이버 사용자 정보를 가져오는데 실패했습니다.");
    }

    // 3) 로그인 또는 회원가입 후 JWT 토큰 생성 및 반환
    public Member loginOrSignUp(String accessToken) {
        NaverUserInfo userInfo = getUserInfo(accessToken);

        // 네이버는 이메일 인증 필드가 없거나 따로 체크 필요할 수 있음
        if (userInfo.response() == null || userInfo.response().email() == null) {
            throw new RuntimeException("이메일 정보가 없는 유저입니다.");
        }

        // 문자열 생년월일 → LocalDate 변환
        String birthdayStr = userInfo.response().birthday();   // MM-dd
        String birthyearStr = userInfo.response().birthyear(); // yyyy

        final LocalDate birthdayFinal;
        if (birthdayStr != null && birthyearStr != null) {
            birthdayFinal = LocalDate.parse(birthyearStr + "-" + birthdayStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            birthdayFinal = null;
        }

        Member member = memberRepository.findByEmail(userInfo.response().email())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(userInfo.response().email())
                        .nickname(userInfo.response().nickname())           // 닉네임 적용
                        .memberPictureUrl(userInfo.response().profileImage())
                        .birthday(birthdayFinal)                             // final 변수 사용
                        .role(Role.ROLE_USER)
                        .provider(Member.Provider.NAVER)
                        .build()));

        return member;
    }

    public Member processLogin(String code, String state) {
        String accessToken = getNaverAccessToken(code, state);
        return loginOrSignUp(accessToken);
    }
}

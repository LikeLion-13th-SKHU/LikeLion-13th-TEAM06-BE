package com.dongnering.oauth2;

import com.dongnering.common.error.ErrorCode;
import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.exception.BusinessException;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.global.jwt.JwtTokenProvider;
import com.dongnering.global.jwt.TokenResDto;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    // accessToken, refreshToken 저장
    public ResponseEntity<ApiResTemplate<TokenResDto>> loginSuccess(Member member) {
        String accessToken = jwtTokenProvider.generateToken(member);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member);

        // DB에 리프레시 토큰 저장
        member.saveRefreshToken(refreshToken);
        memberRepository.save(member);

        // 개인화 여부 응답에 추가
        Boolean profileCompleted = member.getProfileCompleted();

        TokenResDto tokenResDto = new TokenResDto(accessToken, refreshToken, profileCompleted);

        return ResponseEntity.ok()
                .body(ApiResTemplate.successResponse(SuccessCode.LOGIN_SUCCESS, tokenResDto));
    }

    // refreshToken으로 새로운 accessToken 생성
    public ResponseEntity<ApiResTemplate<String>> reissueAccessToken(String refreshToken) {
        Member member = memberRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new BusinessException(ErrorCode.NO_AUTHORIZATION_EXCEPTION
                        , ErrorCode.NO_AUTHORIZATION_EXCEPTION.getMessage()));

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.NO_AUTHORIZATION_EXCEPTION, "리프레시 토큰이 만료되었습니다.");
        }

        String newAccessToken = jwtTokenProvider.generateToken(member);
        return ResponseEntity.ok(ApiResTemplate.successResponse(SuccessCode.REFRESH_TOKEN_SUCCESS, newAccessToken));
    }
}

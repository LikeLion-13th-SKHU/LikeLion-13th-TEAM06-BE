package com.dongnering.member.application;

import com.dongnering.global.jwt.JwtTokenProvider;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.Role;
import com.dongnering.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String loginOrSignUpSocial(String email, String name, String pictureUrl, Member.Provider provider) {
        Member member = memberRepository.findByEmail(email)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(email)
                        .name(name)
                        .pictureUrl(pictureUrl)
                        .role(Role.ROLE_USER)
                        .provider(provider)
                        .build()));

        return jwtTokenProvider.generateToken(member);
    }

}

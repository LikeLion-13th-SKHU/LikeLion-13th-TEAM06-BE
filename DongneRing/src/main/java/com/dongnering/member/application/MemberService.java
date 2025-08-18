package com.dongnering.member.application;

import com.dongnering.member.api.dto.request.MemberUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberInfoResDto getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return new MemberInfoResDto(member);
    }

    @Transactional
    public void updateMemberInfo(Long memberId, MemberUpdateReqDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateProfile(
                request.getNickname(),
                request.getAge(),
                request.getLocation()
        );
    }
}

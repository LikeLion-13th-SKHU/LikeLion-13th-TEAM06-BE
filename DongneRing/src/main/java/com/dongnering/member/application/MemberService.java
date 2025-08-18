package com.dongnering.member.application;

import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import com.dongnering.member.api.dto.request.MemberSaveReqDto;
import com.dongnering.member.api.dto.request.MemberUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.memberInterest.domain.repository.MemberInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final MemberInterestRepository memberInterestRepository;

    //프로필 조회
    @Transactional(readOnly = true)
    public MemberInfoResDto getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        List<String> interests = member.getMemberInterests()
                .stream()
                .map(mi -> mi.getInterest().getInterestType().name())
                .collect(Collectors.toList());

        return MemberInfoResDto.builder()
                .nickname(member.getNickname())
                .age(member.getAge())
                .location(member.getLocation())
                .interests(interests)
                .profileCompleted(member.getProfileCompleted())
                .build();
    }


    // 최초 개인화 저장
    @Transactional
    public void saveProfile(Long memberId, MemberSaveReqDto reqDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateProfile(reqDto.getNickname(), reqDto.getAge(), reqDto.getLocation());

        // 관심사 저장
        for (InterestType interestType : reqDto.getInterests()) {
            Interest interest = interestRepository.findByInterestType(interestType);
            MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interest(interest)
                    .build();
            memberInterestRepository.save(memberInterest);
        }

        // 최초 개인화 완료 처리
        member.completeProfile();
    }

    // 프로필 수정
    @Transactional
    public void updateProfile(Long memberId, MemberUpdateReqDto reqDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        member.updateProfile(reqDto.getNickname(), reqDto.getAge(), reqDto.getLocation());

        // 기존 관심사 제거
        member.getMemberInterests().clear();

        // 새로운 관심사 저장
        for (InterestType interestType : reqDto.getInterests()) {
            Interest interest = interestRepository.findByInterestType(interestType);
            MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interest(interest)
                    .build();
            memberInterestRepository.save(memberInterest);
        }
    }

}

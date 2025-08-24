package com.dongnering.member.application;

import com.dongnering.common.error.ErrorCode;
import com.dongnering.common.exception.BusinessException;
import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import com.dongnering.member.api.dto.request.MemberProfileUpdateReqDto;
import com.dongnering.member.api.dto.request.MemberInterestUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.memberInterest.domain.repository.MemberInterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final MemberInterestRepository memberInterestRepository;

    // Principal에서 Member 조회
    private Member getMemberByPrincipal(Principal principal) {
        Long memberId = Long.parseLong(principal.getName());
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        ErrorCode.MEMBER_NOT_FOUND.getMessage()
                ));
    }

    // 마이페이지 조회
    @Transactional(readOnly = true)
    public MemberInfoResDto getProfile(Principal principal) {
        Member member = getMemberByPrincipal(principal);

        List<String> interests = member.getMemberInterests()
                .stream()
                .map(mi -> mi.getInterest().getInterestType().name())
                .collect(Collectors.toList());

        return MemberInfoResDto.of(
                member.getNickname(),
                member.getEmail(),
                member.getAge(),
                member.getLocation(),
                member.getMemberPictureUrl(),
                interests,
                member.getProfileCompleted(),
                member.getBirthday()
        );

    }

    // 프로필 편집 (닉네임, 이메일)
    @Transactional
    public void updateProfileInfo(Principal principal, MemberProfileUpdateReqDto reqDto) {
        Member member = getMemberByPrincipal(principal);
        // 닉네임과 이메일만 변경
        member.updateProfile(
                reqDto.nickname(),
                member.getAge(),
                member.getLocation(),
                reqDto.email()
        );
    }

    // 관심사/지역 설정
    @Transactional
    public void updateProfileInterest(Principal principal, MemberInterestUpdateReqDto reqDto) {
        Member member = getMemberByPrincipal(principal);

        // location만 변경
        member.updateProfile(
                member.getNickname(),
                member.getAge(),
                reqDto.location(),
                member.getEmail()
        );

        // 기존 관심사 제거
        List<MemberInterest> existingInterests = memberInterestRepository.findByMember(member);
        memberInterestRepository.deleteAll(existingInterests);
        member.getMemberInterests().clear();

        // 새로운 관심사 저장
        for (InterestType interestType : reqDto.interests()) {
            Interest interest = interestRepository.findByInterestType(interestType).orElseThrow(() -> new IllegalStateException("해당하는 관심사가 없습니다."));;;
            if (interest == null) {
                throw new IllegalStateException("해당하는 관심사가 없습니다.");
            }

            MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interest(interest)
                    .build();
            memberInterestRepository.save(memberInterest);
        }
    }
}

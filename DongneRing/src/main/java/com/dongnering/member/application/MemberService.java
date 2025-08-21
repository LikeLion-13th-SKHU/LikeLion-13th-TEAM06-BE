package com.dongnering.member.application;

import com.dongnering.common.error.ErrorCode;
import com.dongnering.common.exception.BusinessException;
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

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
//커밋용
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final MemberInterestRepository memberInterestRepository;

    //공통적으로 Member 조회 메서드
    private Member getMemberByPrincipal(Principal principal) {
        Long memberId = Long.parseLong(principal.getName()); // 토큰에서 memberId 추출
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.MEMBER_NOT_FOUND,
                        ErrorCode.MEMBER_NOT_FOUND.getMessage()
                ));
    }


    //프로필 조회
    @Transactional(readOnly = true)
    public MemberInfoResDto getProfile(Principal principal) {
        Member member = getMemberByPrincipal(principal);

        List<String> interests = member.getMemberInterests()
                .stream()
                .map(mi -> mi.getInterest().getInterestType().name())
                .collect(Collectors.toList());

        return MemberInfoResDto.of(
                member.getNickname(),
                member.getAge(),
                member.getLocation(),
                member.getMemberPictureUrl(),
                interests,
                member.getProfileCompleted()
        );
    }

    //최초 개인화 저장
    @Transactional
    public void saveProfile(Principal principal, MemberSaveReqDto reqDto) {
        Member member = getMemberByPrincipal(principal);

        member.updateProfile(reqDto.nickname(), reqDto.age(), reqDto.location());

        //관심사 저장
        for (InterestType interestType : reqDto.interests()) {
            Interest interest = interestRepository.findByInterestType(interestType).orElseThrow(() -> new IllegalStateException("해당하는 관심사가 없습니다."));;
            MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interest(interest)
                    .build();
            memberInterestRepository.save(memberInterest);
        }

        //최초 개인화 완료 처리
        member.completeProfile();
    }

    //프로필 수정
    @Transactional
    public void updateProfile(Principal principal, MemberUpdateReqDto reqDto) {
        Member member = getMemberByPrincipal(principal);

        member.updateProfile(reqDto.nickname(), reqDto.age(), reqDto.location());

        //기존 관심사 제거
        memberInterestRepository.deleteAll(member.getMemberInterests());
        member.getMemberInterests().clear();

        //새로운 관심사 저장
        for (InterestType interestType : reqDto.interests()) {
            Interest interest = interestRepository.findByInterestType(interestType).orElseThrow(() -> new IllegalStateException("해당하는 관심사가 없습니다."));;;
            MemberInterest memberInterest = MemberInterest.builder()
                    .member(member)
                    .interest(interest)
                    .build();
            memberInterestRepository.save(memberInterest);
        }
    }
}

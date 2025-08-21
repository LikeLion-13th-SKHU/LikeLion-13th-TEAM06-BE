package com.dongnering.oauth2.common.application;

import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import com.dongnering.interest.domain.repository.InterestRepository;
import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.memberInterest.domain.repository.MemberInterestRepository;
import com.dongnering.member.domain.Member;
import com.dongnering.member.domain.repository.MemberRepository;
import com.dongnering.oauth2.common.api.dto.LoginFirstAdditionalInfoRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;


//최초 로그인 후 추가 정보 처리 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class LoginFirstAdditionalInfoService {

    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final MemberInterestRepository memberInterestRepository;

    @Transactional
    public void loginFirstAdditionalInfo(LoginFirstAdditionalInfoRequestDto loginFirstAdditionalInfoRequestDto, Principal principal){

        Long memberId = Long.parseLong(principal.getName());

        Member member = memberRepository.findById(memberId).orElseThrow(()-> new IllegalStateException("해당 멤버가 없습니다. id=" + memberId));

        member.setLocation(loginFirstAdditionalInfoRequestDto.location());
        member.setAge(loginFirstAdditionalInfoRequestDto.memberAge());

        List<InterestType> interestTypes = loginFirstAdditionalInfoRequestDto.interestTypeList();

        for (InterestType interestType : interestTypes) {


            if(interestRepository.existsByInterestType(interestType)){

                Interest interest = interestRepository.findByInterestType(interestType).orElseThrow(() -> new IllegalStateException("해당하는 관심사가 없습니다."));;

                MemberInterest memberInterest = new MemberInterest(member, interest);
                memberInterestRepository.save(memberInterest);
                member.getMemberInterests().add(memberInterest);

            };

        }

    }




}

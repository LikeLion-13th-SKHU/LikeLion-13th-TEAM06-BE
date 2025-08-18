package com.dongnering.member.api;

import com.dongnering.member.api.dto.request.MemberSaveReqDto;
import com.dongnering.member.api.dto.request.MemberUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.application.MemberService;
import com.dongnering.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

   //프로필 조회
    @GetMapping("/info")
    public MemberInfoResDto getProfile(@AuthenticationPrincipal(expression = "member") Member member) {
        // Principal에서 바로 Member 객체를 가져와 memberId 사용
        return memberService.getProfile(member.getMemberId());
    }

    //최초 개인화 정보 저장
    @PostMapping("/info")
    public void saveProfile(@AuthenticationPrincipal(expression = "member") Member member,
                            @RequestBody MemberSaveReqDto reqDto) {
        memberService.saveProfile(member.getMemberId(), reqDto);
    }

    //프로필 수정
    @PutMapping("/info")
    public void updateProfile(@AuthenticationPrincipal(expression = "member") Member member,
                              @RequestBody MemberUpdateReqDto reqDto) {
        memberService.updateProfile(member.getMemberId(), reqDto);
    }
}

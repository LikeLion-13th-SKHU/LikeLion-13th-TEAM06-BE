package com.dongnering.member.api;

import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.member.api.dto.request.MemberOnBordingUpdateReqDto;
import com.dongnering.member.api.dto.request.MemberProfileUpdateReqDto;
import com.dongnering.member.api.dto.request.MemberInterestUpdateReqDto;
import com.dongnering.member.api.dto.response.MemberInfoResDto;
import com.dongnering.member.application.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService memberService;

    // 마이페이지 조회
    @GetMapping("/info")
    @Operation(summary = "마이페이지 - 멤버 정보 조회", description = "마이페이지 - 멤버 정보 조회")
    public ApiResTemplate<MemberInfoResDto> getProfile(Principal principal) {
        return ApiResTemplate.successResponse(
                SuccessCode.GET_SUCCESS,
                memberService.getProfile(principal)
        );
    }

    // 프로필 편집 (닉네임, 이메일)
    @PutMapping("/profile")
    @Operation(summary = "프로필 편집", description = "이메일, 닉네임 변경")
    public ApiResTemplate<Void> updateProfileInfo(
            Principal principal,
            @RequestBody MemberProfileUpdateReqDto reqDto
    ) {
        memberService.updateProfileInfo(principal, reqDto);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }

    // 관심사/지역 설정
    @PutMapping("/interest")
    @Operation(summary = "관심사/지역 설정", description = "지역, 관심사 변경")
    public ApiResTemplate<Void> updateProfileInterest(
            Principal principal,
            @RequestBody MemberInterestUpdateReqDto reqDto
    ) {
        memberService.updateProfileInterest(principal, reqDto);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }



    //온보딩용 입력 api
    @PutMapping("/onbording")
    @Operation(summary = "온보딩 설정 ", description = "온보딩 설정")
    public ApiResTemplate<Void> onBording(
            Principal principal,
            @RequestBody MemberOnBordingUpdateReqDto reqDto
    ) {
        memberService.onBording(principal, reqDto );
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }


}

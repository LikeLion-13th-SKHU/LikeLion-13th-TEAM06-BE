package com.dongnering.member.api;

import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.member.api.dto.request.MemberSaveReqDto;
import com.dongnering.member.api.dto.request.MemberUpdateReqDto;
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

    //마이페이지 조회
    @GetMapping("/info")
    @Operation(summary = "마이페이지 - 멤버 정보 조회(이름, 나이, 지역, 사진url, 관심사)", description = "마이페이지 - 멤버 정보 조회(이름, 나이, 지역, 사진url, 관심사)")
    public ApiResTemplate<MemberInfoResDto> getProfile(Principal principal) {
        return ApiResTemplate.successResponse(
                SuccessCode.GET_SUCCESS,
                memberService.getProfile(principal)
        );
    }

    //최초 개인화 저장
    @PostMapping("/info")
    @Operation(summary = "최초 로그인 후 정보저장(이름, 나이, 지역, 관심사)", description = "최초 로그인 후 정보저장(이름, 나이, 지역, 관심사)")
    public ApiResTemplate<Void> saveProfile(
            Principal principal,
            @RequestBody MemberSaveReqDto reqDto
    ) {
        memberService.saveProfile(principal, reqDto);
        return ApiResTemplate.successResponse(SuccessCode.MEMBER_FIRST_UPDATE_SUCCESS, null);
    }

    //프로필 수정
    @PutMapping("/info")
    @Operation(summary = "프로필 수정", description = "프로필 수정")
    public ApiResTemplate<Void> updateProfile(
            Principal principal,
            @RequestBody MemberUpdateReqDto reqDto
    ) {
        memberService.updateProfile(principal, reqDto);
        return ApiResTemplate.successResponse(SuccessCode.UPDATE_SUCCESS, null);
    }
}

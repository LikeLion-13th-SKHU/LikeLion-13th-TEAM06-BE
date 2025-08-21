package com.dongnering.oauth2.common.api;

import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.oauth2.common.api.dto.LoginFirstAdditionalInfoRequestDto;
import com.dongnering.oauth2.common.application.LoginFirstAdditionalInfoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

//최초 로그인 후 추가 정보 컨트롤러

@RestController
@RequiredArgsConstructor
public class LoginFirstAdditionalInfoController {

    private final LoginFirstAdditionalInfoService loginFirstAdditionalInfoService;



    @PostMapping("/loginFirst")
    @Operation(summary = "이거 사용하지 마시오 -> 위에 멤버 컨트롤러사용", description = "이거 사용하지 마시오")
    public ApiResTemplate<String> firstLoginAdditionalInfoAdd(@RequestBody LoginFirstAdditionalInfoRequestDto loginFirstAdditionalInfoRequestDto, Principal principal){
        loginFirstAdditionalInfoService.loginFirstAdditionalInfo(loginFirstAdditionalInfoRequestDto, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_FIRST_UPDATE_SUCCESS);
    }

}

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
    @Operation(summary = "최초 로그인 후 추가정보(나이, 관심사, 지역 추가하는 url", description = "최초 로그인 후 추가정보(나이, 관심사, 지역 추가하는 url")
    public ApiResTemplate<String> firstLoginAdditionalInfoAdd(@RequestBody LoginFirstAdditionalInfoRequestDto loginFirstAdditionalInfoRequestDto, Principal principal){
        loginFirstAdditionalInfoService.loginFirstAdditionalInfo(loginFirstAdditionalInfoRequestDto, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_FIRST_UPDATE_SUCCESS);
    }

}

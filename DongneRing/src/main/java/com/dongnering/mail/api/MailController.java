package com.dongnering.mail.api;

import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.mail.scheduler.NewsScheduler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@Tag(name = "이메일 전송 TEST API", description = "뉴스레터 이메일 전송 기능 관련")
public class MailController {

    private final NewsScheduler newsScheduler;

    @Operation(summary = "메일 전송 TEST api", description = "이메일 전송 테스트를 위한 api입니다. " +
            "<br> 로그인 한 사용자가 해당 api를 호출하면 가입한 이메일로 뉴스 레터가 전송됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "응답 생성에 성공하였습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @GetMapping("/news/email/test")
    public ApiResTemplate<String> sendEmail(Principal principal) {
        newsScheduler.sendDailyNewsWithMember(principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.EMAIL_SEND_SUCCESS);
    }
}

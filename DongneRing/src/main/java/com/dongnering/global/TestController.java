package com.dongnering.global;


import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/test")
    public ApiResTemplate<String> test() {
        return ApiResTemplate.successResponse(SuccessCode.GET_SUCCESS, "Test");
    }
}

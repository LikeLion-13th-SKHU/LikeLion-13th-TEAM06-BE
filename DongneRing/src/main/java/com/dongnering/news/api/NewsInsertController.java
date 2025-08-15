package com.dongnering.news.api;


import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import com.dongnering.news.api.dto.request.NewsSaveDto;
import com.dongnering.news.application.NewsInsertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/newsinsert")
public class NewsInsertController {

    private final NewsInsertService newsService;

    @PostMapping("/save")
    public ApiResTemplate<String> newsSave(@RequestBody NewsSaveDto newsSaveDto){
        newsService.newsSave(newsSaveDto);
        return ApiResTemplate.successWithNoContent(SuccessCode.MEMBER_SAVE_SUCCESS);
    }




}

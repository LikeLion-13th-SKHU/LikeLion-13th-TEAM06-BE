package com.dongnering.comment.newsComment.api;


import com.dongnering.comment.newsComment.api.dto.requsest.NewsCommentRequestDto;
import com.dongnering.comment.newsComment.application.NewsCommentService;
import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class NewsCommentController {

    private final NewsCommentService newsCommentService;

    @PostMapping("newsComment")
    @Operation(summary = "뉴스 댓글 생성 ", description = "뉴스 댓글 생성")
    public ApiResTemplate<String> commentCreate(@RequestBody NewsCommentRequestDto newsCommentRequestDto, Principal principal){


        newsCommentService.createNewsComment(newsCommentRequestDto, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.COMMENT_SAVE_SUCCESS);
    }

    @DeleteMapping("newsComment/{newsId}")
    @Operation(summary = "뉴스 댓글 삭제 ", description = "뉴스 댓글 삭제")
    public ApiResTemplate<String> commentDelete(@PathVariable Long newsId ,Principal principal){
        newsCommentService.deleteNewsComment(newsId, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.COMMENT_DELETE_SUCCESS);
    }






}

package com.dongnering.comment.artComment.api;


import com.dongnering.comment.artComment.api.dto.request.ArtCommentRequestDto;
import com.dongnering.comment.artComment.application.ArtCommentService;
import com.dongnering.common.error.SuccessCode;
import com.dongnering.common.template.ApiResTemplate;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ArtCommentController {

    private final ArtCommentService artCommentService;

    //아트 댓글 생성
    @PostMapping("/artComment")
    @Operation(summary = "아트 댓글 생성 ", description = "아트 댓글 생성")
    public ApiResTemplate<String> commentCreate(@RequestBody ArtCommentRequestDto artCommentRequestDto, Principal principal){
        artCommentService.createArtComment(artCommentRequestDto, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.COMMENT_SAVE_SUCCESS);
    }

    //아트 댓글 삭제
    @DeleteMapping("/artComment/{newsId}")
    @Operation(summary = "아트 댓글 삭제 ", description = "아트 댓글 삭제")
    public ApiResTemplate<String> commentDelete(@PathVariable Long newsId, Principal principal){
        artCommentService.deleteArtComment(newsId, principal);
        return ApiResTemplate.successWithNoContent(SuccessCode.COMMENT_DELETE_SUCCESS);
    }
}
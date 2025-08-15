package com.dongnering.comment.newsComment.api.dto.response;


import com.dongnering.comment.newsComment.domain.NewsComment;
import lombok.Builder;

@Builder
public record NewsCommentResponseDto(

        Long newsCommentId,
        String memberName,
        String content,
        String memberImageUrl

) {


    public static NewsCommentResponseDto from(NewsComment newsComment) {
        return NewsCommentResponseDto.builder()
                .newsCommentId(newsComment.getNewsCommentId())
                .memberName(newsComment.getMember().getNickname()) // Member 엔티티에 name 필드 있다고 가정
                .content(newsComment.getContent())
                .memberImageUrl(newsComment.getMember().getMemberPictureUrl()) // Member 엔티티에 imageUrl 필드 있다고 가정
                .build();
    }
}

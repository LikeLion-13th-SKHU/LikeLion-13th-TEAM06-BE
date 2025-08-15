package com.dongnering.comment.artComment.api.dto.response;


import com.dongnering.comment.artComment.domain.ArtComment;
import lombok.Builder;

@Builder
public record ArtCommentResponseDto(

        Long artCommentId,
        String memberName,
        String content,
        String memberImageUrl

) {

    public static ArtCommentResponseDto from(ArtComment artComment){
        return ArtCommentResponseDto.builder()
                .artCommentId(artComment.getArtCommentId())
                .memberName(artComment.getMember().getNickname())
                .content(artComment.getContent())
                .memberImageUrl(artComment.getMember().getMemberPictureUrl())
                .build();
    }





}

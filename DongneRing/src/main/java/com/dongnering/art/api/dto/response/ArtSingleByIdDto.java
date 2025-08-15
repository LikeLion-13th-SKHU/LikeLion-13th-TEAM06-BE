package com.dongnering.art.api.dto.response;


import com.dongnering.art.domain.Art;
import com.dongnering.comment.artComment.api.dto.response.ArtCommentResponseDto;
import lombok.Builder;

import java.util.List;

//단건 조회시 사용
@Builder
public record ArtSingleByIdDto(

        Long artId,
        String title,
        String startDate,
        String endDate,

        //장소 - ex)문화회관
        String area,

        //지역 - ex) 서울, 경기
        String location,

        String imageUrl,
        Long likeCount,
        boolean liked,

        List<ArtCommentResponseDto> artComments

) {

    public static ArtSingleByIdDto from(Art art, boolean liked, List<ArtCommentResponseDto> artCommentResponseDtos){
        return ArtSingleByIdDto.builder()
                .artId(art.getArtId())
                .title(art.getTitle())
                .startDate(art.getStartDate())
                .endDate(art.getEndDate())
                .area(art.getArea())
                .location(art.getLocation())
                .imageUrl(art.getImageUrl())
                .likeCount(art.getLikeCount())
                .liked(liked)
                .artComments(artCommentResponseDtos)
                .build();

    }




}

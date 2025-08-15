package com.dongnering.art.api.dto.response;


import com.dongnering.art.domain.Art;
import lombok.Builder;

//아트 전체 조회시 사용
@Builder
public record ArtListDto(

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

        Long artCommentCount

) {

    public static ArtListDto from(Art art, boolean liked, Long artCommentCount){
        return ArtListDto.builder()
                .artId(art.getArtId())
                .title(art.getTitle())
                .startDate(art.getStartDate())
                .endDate(art.getEndDate())
                .area(art.getArea())
                .location(art.getLocation())
                .imageUrl(art.getImageUrl())
                .likeCount(art.getLikeCount())
                .liked(liked)
                .artCommentCount(artCommentCount)
                .build();

    }




}

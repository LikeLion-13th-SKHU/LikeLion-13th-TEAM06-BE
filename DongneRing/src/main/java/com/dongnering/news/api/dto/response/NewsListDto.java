package com.dongnering.news.api.dto.response;


import com.dongnering.interest.domain.InterestType;
import com.dongnering.news.domain.News;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsListDto(
        Long newsId,

        String title,

        String content,

        List<InterestType> interestTypes,


        String newsDate,

        List<String> tag,


        String imgUrl,

        Long likeCount,

        boolean liked,

        Long newsCommentNumber


) {

    public static NewsListDto from(News news, boolean liked, Long newsCommentNumber, List<InterestType> interestTypes){
        return NewsListDto.builder()
                .newsId(news.getNewsId())
                .title(news.getTitle())
                .interestTypes(interestTypes)
                .tag(news.getNewsTags())
                .content(news.getContent())
                .newsDate(news.getNewsdate())
                .imgUrl(news.getImgUrl())
                .likeCount(news.getLikeCount())
                .liked(liked)
                .newsCommentNumber(newsCommentNumber)
                .build();
    }

}

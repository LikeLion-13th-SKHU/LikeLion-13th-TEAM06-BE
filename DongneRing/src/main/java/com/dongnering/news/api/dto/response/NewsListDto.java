package com.dongnering.news.api.dto.response;


import com.dongnering.news.domain.News;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsListDto(
        Long newsId,

        String title,

        String content,

        String newsDate,

        List<String> tag,


        String imgUrl,

        Long likeCount,

        boolean liked,

        Long newsCommentNumber


) {

    public static NewsListDto from(News news, boolean liked, Long newsCommentNumber){
        return NewsListDto.builder()
                .newsId(news.getNewsId())
                .title(news.getTitle())
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

package com.dongnering.news.api.dto.response;


import com.dongnering.comment.newsComment.api.dto.response.NewsCommentResponseDto;
import com.dongnering.news.domain.News;
import lombok.Builder;

import java.util.List;

@Builder
public record NewsSingleByIdDto(
        Long newsId,

        String title,

        String content,

        String newsDate,

        String imgUrl,

        Long likeCount,

        boolean liked,

        List<NewsCommentResponseDto> newsComment,

        List<String> summary


) {

    public static NewsSingleByIdDto from(News news, boolean liked, List<NewsCommentResponseDto> newsComment){
        return NewsSingleByIdDto.builder()
                .newsId(news.getNewsId())
                .title(news.getTitle())
                .summary(news.getNewsSummary())
                .content(news.getContent())
                .newsDate(news.getNewsdate())
                .imgUrl(news.getImgUrl())
                .likeCount(news.getLikeCount())
                .liked(liked)
                .newsComment(newsComment)
                .build();
    }

}

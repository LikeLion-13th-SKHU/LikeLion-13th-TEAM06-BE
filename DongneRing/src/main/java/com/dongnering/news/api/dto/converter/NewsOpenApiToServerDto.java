package com.dongnering.news.api.dto.converter;

public record NewsOpenApiToServerDto(
        String newsIdentifyId,
        String title,
       String contents,
       String pictureUrl

) {
}

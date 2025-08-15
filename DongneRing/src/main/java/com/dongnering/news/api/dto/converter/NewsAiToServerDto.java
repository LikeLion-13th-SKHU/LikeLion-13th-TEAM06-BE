package com.dongnering.news.api.dto.converter;


import com.dongnering.interest.domain.InterestType;

import java.util.List;

public record NewsAiToServerDto(

        Long NewsItemId,
        String title,
        String contents,
        String pictureUrl,
        String location,
        List<InterestType> interestTypes

) {
}

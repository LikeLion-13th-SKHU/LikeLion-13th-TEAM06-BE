package com.dongnering.news.api.dto.request;


import com.dongnering.interest.domain.InterestType;

import java.util.List;

public record NewsSaveDto(

        String title,
        String contents,
        String imageUrl,
        String location,
        List<InterestType> interestTypes

) {
}

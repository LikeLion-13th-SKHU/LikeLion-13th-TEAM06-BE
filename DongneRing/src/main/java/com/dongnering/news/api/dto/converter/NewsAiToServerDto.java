package com.dongnering.news.api.dto.converter;


import com.dongnering.interest.domain.InterestType;
import lombok.Builder;

import java.util.List;
@Builder
public record NewsAiToServerDto(

        Long newsIdentifyId ,
        List<InterestType> category,
        List<String> summary,
        List<String> tag,
        String location

) {
}

package com.dongnering.news.api.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record NewsUpdateDtoDDDDDD (

        List<String> category,
        Long newsId,
        List<String> summary,
        List<String> tag,
        String location

){

}

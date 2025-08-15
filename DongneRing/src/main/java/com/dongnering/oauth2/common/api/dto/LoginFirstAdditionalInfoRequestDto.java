package com.dongnering.oauth2.common.api.dto;

import com.dongnering.interest.domain.InterestType;

import java.util.List;

//최초 로그인 후 추가 정보 적는 dto
public record LoginFirstAdditionalInfoRequestDto(

        Long memberAge,
        String location,
        List<InterestType> interestTypeList

) {

}

package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record MemberInterestUpdateReqDto(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate birthDate,
        String location,
        List<InterestType> interests
) {}

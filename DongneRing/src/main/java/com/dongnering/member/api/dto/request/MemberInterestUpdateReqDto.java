package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import java.util.List;

public record MemberInterestUpdateReqDto(
        String location,
        List<InterestType> interests
) {}

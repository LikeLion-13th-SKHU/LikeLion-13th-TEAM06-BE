package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;

import java.util.List;

public record MemberOnBordingUpdateReqDto(
        String location,
        List<InterestType> interests
) {}

package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import java.util.List;

//최초 개인화저장용
public record MemberSaveReqDto(
        String nickname,
        Long age,
        String location,
        List<InterestType> interests
) {}

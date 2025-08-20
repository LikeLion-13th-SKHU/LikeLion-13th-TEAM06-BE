package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import java.util.List;

//프로필 수정용
public record MemberUpdateReqDto(
        String nickname,
        Long age,
        String location,
        List<InterestType> interests
) {}

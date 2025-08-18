package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//프로필 수정용
@Getter
@NoArgsConstructor
public class MemberUpdateReqDto {
    private String nickname;
    private Long age;
    private String location;
    List<InterestType> interests;
}

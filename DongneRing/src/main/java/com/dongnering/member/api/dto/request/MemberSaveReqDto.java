package com.dongnering.member.api.dto.request;

import com.dongnering.interest.domain.InterestType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//최초 개인화 저장용
@Getter
@NoArgsConstructor
public class MemberSaveReqDto {
    private String nickname;
    private Long age;
    private String location;
    List<InterestType> interests;
}

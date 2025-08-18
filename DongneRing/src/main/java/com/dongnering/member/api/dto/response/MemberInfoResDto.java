package com.dongnering.member.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

//조회용
@Getter
@Builder
public class MemberInfoResDto {
    private String nickname;
    private Long age;
    private String location;
    private List<String> interests;
    private Boolean profileCompleted;
}

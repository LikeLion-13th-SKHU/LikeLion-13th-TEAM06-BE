package com.dongnering.member.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateReqDto {

    private String nickname;
    private Long age;
    private String location;

    // 선택적으로 관심사나 프로필 사진 등 추가 가능
}

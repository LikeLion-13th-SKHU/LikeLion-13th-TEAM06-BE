package com.dongnering.member.api.dto.response;

import com.dongnering.member.domain.Member;
import com.dongnering.memberInterest.domain.MemberInterest;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class MemberInfoResDto {

    private String nickname;
    private Long age;
    private String location;
    private List<String> interests; // 관심사 추가

    public MemberInfoResDto(Member member) {
        this.nickname = member.getNickname();
        this.age = member.getAge();
        this.location = member.getLocation();
        this.interests = member.getMemberInterests()
                .stream()
                .map(mi -> mi.getInterest().getName()) // MemberInterest → Interest → name
                .collect(Collectors.toList());
    }
}

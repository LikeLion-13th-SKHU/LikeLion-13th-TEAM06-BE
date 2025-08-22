package com.dongnering.member.api.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberInfoResDto(
        String nickname,
        String email,
        Long age,
        String location,
        String memberPictureUrl,
        List<String> interests,
        boolean profileCompleted
) {
    // 정적 팩토리 메서드
    public static MemberInfoResDto of(String nickname, String email, Long age, String location,
                                      String memberPictureUrl, List<String> interests, boolean profileCompleted) {
        return new MemberInfoResDto(nickname, email, age, location, memberPictureUrl, interests, profileCompleted);
    }
}

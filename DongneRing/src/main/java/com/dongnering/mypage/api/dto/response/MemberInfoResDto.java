package com.dongnering.mypage.api.dto.response;

import com.dongnering.mypage.domain.User;

public record MemberInfoResDto(
        String email,
        String name,
        String token
) {
    public static MemberInfoResDto of(User member, String token) {
        return new MemberInfoResDto(
                member.getEmail(),
                member.getNickname(),
                token
        );
    }
}

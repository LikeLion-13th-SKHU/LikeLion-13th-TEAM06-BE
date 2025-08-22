package com.dongnering.member.api.dto.request;

public record MemberProfileUpdateReqDto(
        String nickname,
        String email
) {}

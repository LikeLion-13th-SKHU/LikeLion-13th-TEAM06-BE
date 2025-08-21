package com.dongnering.global.jwt;

public record TokenResDto(
        String accessToken,
        String refreshToken,
        Boolean profileCompleted
) {
}

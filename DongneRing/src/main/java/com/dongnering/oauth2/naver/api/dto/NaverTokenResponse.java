package com.dongnering.oauth2.naver.api.dto;

import com.google.gson.annotations.SerializedName;

public record NaverTokenResponse(
        @SerializedName("access_token")
        String accessToken,

        @SerializedName("refresh_token")
        String refreshToken,

        @SerializedName("token_type")
        String tokenType,

        @SerializedName("expires_in")
        String expiresIn
) {}

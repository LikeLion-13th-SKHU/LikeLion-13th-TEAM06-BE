package com.dongnering.oauth2.naver.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class NaverTokenResponse {
    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("refresh_token")
    private String refreshToken;

    @SerializedName("token_type")
    private String tokenType;

    @SerializedName("expires_in")
    private String expiresIn;
}

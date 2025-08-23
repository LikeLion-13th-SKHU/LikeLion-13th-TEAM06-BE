package com.dongnering.oauth2.naver.api.dto;

import com.google.gson.annotations.SerializedName;

public record NaverUserInfo(Response response) {

    public record Response(
            String email,

            String nickname,
            @SerializedName("profile_image")
            String profileImage,

            String name,

            String birthday,

            String birthyear

    ) {}
}

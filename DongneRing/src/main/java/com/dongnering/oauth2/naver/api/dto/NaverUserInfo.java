package com.dongnering.oauth2.naver.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class NaverUserInfo {

    private Response response;

    @Data
    public static class Response {
        private String email;
        private String nickname;

        @SerializedName("profile_image")
        private String profileImage;

        private String name;
    }
}

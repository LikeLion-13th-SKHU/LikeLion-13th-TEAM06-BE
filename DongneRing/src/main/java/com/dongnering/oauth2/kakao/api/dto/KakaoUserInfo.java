package com.dongnering.oauth2.kakao.api.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class KakaoUserInfo {
    private Long id;

    @SerializedName("kakao_account")
    private KakaoAccount kakaoAccount;

    @Data
    public static class KakaoAccount {
        private String email;

        @SerializedName("is_email_valid")
        private Boolean isEmailValid;

        @SerializedName("is_email_verified")
        private Boolean isEmailVerified;

        private Profile profile;
    }

    @Data
    public static class Profile {
        private String nickname;

        @SerializedName("profile_image_url")
        private String profileImageUrl;

        @SerializedName("thumbnail_image_url")
        private String thumbnailImageUrl;
    }
}

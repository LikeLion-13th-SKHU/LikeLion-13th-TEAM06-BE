package com.dongnering.member.domain;

import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.comment.newsComment.domain.NewsComment;
import com.dongnering.memberArtLike.MemberArtLike;
import com.dongnering.memberNewsLike.MemberNewsLike;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class Member {

    public enum Provider {
        GOOGLE, NAVER, KAKAO, LOCAL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String nickname;

    private Long age;

    private String memberPictureUrl;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String refreshToken;

    //최초 개인화 입력 여부
    @Column(nullable = false)
    private boolean profileCompleted = false;

    //멤버 - 관심사태그
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberInterest> memberInterests = new ArrayList<>();

    //멤버 - 뉴스 좋아요
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberNewsLike> newsLikeList = new ArrayList<>();

    //멤버 - 예술 좋아요
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberArtLike> memberArtLikes = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsComment> newsCommentList = new ArrayList<>();

    @Builder
    private Member(String nickname, String email, String memberPictureUrl, Role role, Provider provider, String refreshToken) {
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
        this.memberPictureUrl = memberPictureUrl;
        this.refreshToken = refreshToken;
    }

    public void saveRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    //프로필 수정용 메서드
    public void updateProfile(String nickname, Long age, String location, String email) {
        if (nickname != null) this.nickname = nickname;
        if (age != null) this.age = age;
        if (location != null) this.location = location;
        if (email != null) this.email = email;
    }

    // 개인화 저장용 메서드
    public void saveProfile(Long age, String location) {
        if (age != null) this.age = age;
        if (location != null) this.location = location;
    }

    // 개인화 완료 처리 메서드
    public void completeProfile() {
        this.profileCompleted = true;
    }

    // boolean 필드용 메서드 추가
    public boolean getProfileCompleted() {
        return this.profileCompleted;
    }
}

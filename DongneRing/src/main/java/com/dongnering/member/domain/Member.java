package com.dongnering.member.domain;

import com.dongnering.comment.newsComment.domain.NewsComment;
import com.dongnering.memberArtLike.MemberArtLike;
import com.dongnering.memberInterest.domain.MemberInterest;
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
    private Member(String nickname, String email, String memberPictureUrl , Role role, Provider provider){
        this.nickname = nickname;
        this.email =email;
        this.role =role;
        this.provider = provider;
        this.memberPictureUrl = memberPictureUrl;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAge(Long age) {
        this.age = age;
    }
}

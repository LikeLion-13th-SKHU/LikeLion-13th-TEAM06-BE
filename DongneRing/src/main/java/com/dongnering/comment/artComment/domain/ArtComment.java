package com.dongnering.comment.artComment.domain;

import com.dongnering.art.domain.Art;
import com.dongnering.mypage.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ArtComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_comment_id")
    private Long artCommentId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    Member member;

    @ManyToOne
    @JoinColumn(name = "art_id")
    Art art;

    private String content;

    @Builder
    public ArtComment(Member member, Art art, String content) {
        this.member = member;
        this.art = art;
        this.content = content;
    }
}

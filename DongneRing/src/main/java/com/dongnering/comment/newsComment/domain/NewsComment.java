package com.dongnering.comment.newsComment.domain;

import com.dongnering.mypage.domain.Member;
import com.dongnering.news.domain.News;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NewsComment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_comment_id")
    private Long newsCommentId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    //댓글 내용
    private String content;

    @Builder
    public NewsComment(Member member, News news, String content) {
        this.member = member;
        this.news = news;
        this.content = content;
    }
}

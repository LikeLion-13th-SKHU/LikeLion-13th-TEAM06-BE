package com.dongnering.news.domain;


import com.dongnering.memberNewsLike.MemberNewsLike;
import com.dongnering.newsInterest.domain.NewsInterest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_id")
    private Long newsId;

    //db뉴스 식별용 - openapi에서 달아준값
    private Long newsIdentifyId;

    private String title;

    @Lob
    private String content;

    private String imgUrl;



    //뉴스 요약
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "news_summary_list", joinColumns = @JoinColumn(name = "news_id"))
    @Column(name = "news_summary_text",  columnDefinition = "TEXT")
    private List<String> newsSummary = new ArrayList<>();

    //카테고리
    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NewsInterest> newsInterest = new ArrayList<>();

    //태그
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "news_tag_list", joinColumns = @JoinColumn(name = "news_id"))
    @Column(name = "news_tag_text")
    private List<String> newsTags = new ArrayList<>();

    //지역
    private String location;

    //좋아요개수
    private Long likeCount;

    private String newsdate;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberNewsLike>  MemberNewsLike = new ArrayList<>();

    @Builder
    public News(Long newsIdentifyId, String title, String content, String imgUrl, String newsdate) {
        this.newsIdentifyId = newsIdentifyId;
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.likeCount = 0L;
        this.newsdate = newsdate;

    }


    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void setLocation(String location){this.location = location;}






}

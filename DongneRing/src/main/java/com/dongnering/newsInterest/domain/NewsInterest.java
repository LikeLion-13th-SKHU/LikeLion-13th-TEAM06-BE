package com.dongnering.newsInterest.domain;


import com.dongnering.interest.domain.Interest;
import com.dongnering.news.domain.News;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_interest_id")
    private Long newsInterestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    private News news;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;


    @Builder
    public NewsInterest(News news, Interest interest){
        this.news = news;
        this.interest = interest;
    }



}

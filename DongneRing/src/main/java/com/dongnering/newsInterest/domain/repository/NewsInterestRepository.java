package com.dongnering.newsInterest.domain.repository;


import com.dongnering.interest.domain.InterestType;
import com.dongnering.news.domain.News;
import com.dongnering.newsInterest.domain.NewsInterest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NewsInterestRepository extends JpaRepository<NewsInterest, Long> {

    @Query("SELECT n.interest.interestType FROM NewsInterest n WHERE n.news = :news")
    List<InterestType> findInterestTypesByNews(@Param("news") News news);
}

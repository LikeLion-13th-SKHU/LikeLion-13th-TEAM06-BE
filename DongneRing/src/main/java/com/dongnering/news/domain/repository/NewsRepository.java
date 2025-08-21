package com.dongnering.news.domain.repository;


import com.dongnering.interest.domain.Interest;
import com.dongnering.news.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Long> {

    //장소별 조회 - ex)서울 -> 서울 + 전국
    @Query("select n from News n where n.location = :location or n.location = '전국'")
    Page<News> findByLocation(String location, Pageable pageable);

    //멤버 관심사별 조회
    @Query("""
            SELECT DISTINCT n FROM News n
            JOIN n.newsInterest ni
            JOIN ni.interest i
            WHERE i IN :interests""")
    Page<News> findByInterests(@Param("interests") List<Interest> interests, Pageable pageable);


    //장소별 + 개인 관심사멸
    @Query("""
    SELECT DISTINCT n FROM News n
    JOIN n.newsInterest ni
    JOIN ni.interest i
    WHERE (n.location = :location OR n.location = '전국')
    AND i IN :interests""")
    Page<News> findByLocationAndInterests(@Param("location") String location, @Param("interests") List<Interest> interests, Pageable pageable);

    Page<News> findAllByOrderByLikeCountDesc(Pageable pageable);

    @Query("select n from News n where n.newsId in :personalCommentList")
    Page<News> findNewsByCommentId(@Param("personalCommentList") List<Long> personalCommentList, Pageable pageable);


    boolean existsByNewsIdentifyId(Long newsIdentifyId);

    Optional<News> findByNewsIdentifyId(Long newsIdentifyId);
}

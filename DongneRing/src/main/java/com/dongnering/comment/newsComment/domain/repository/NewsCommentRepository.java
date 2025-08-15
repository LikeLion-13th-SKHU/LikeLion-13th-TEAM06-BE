package com.dongnering.comment.newsComment.domain.repository;

import com.dongnering.comment.newsComment.domain.NewsComment;
import com.dongnering.mypage.domain.Member;
import com.dongnering.news.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsCommentRepository extends JpaRepository<NewsComment, Long> {

    boolean existsByMemberAndNews(Member member, News news);

    NewsComment findByMemberAndNews(Member member, News news);

    List<NewsComment> findAllByNews(News news);


    Long countByNews(News news);
}



package com.dongnering.memberNewsLike;


import com.dongnering.mypage.domain.Member;
import com.dongnering.news.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberNewsLikeRepository extends JpaRepository<MemberNewsLike, Long> {

    boolean existsByMemberAndNews(Member member, News news);

    void deleteByMemberAndNews(Member member, News news);

    @Query("select m.news.newsId from MemberNewsLike m where m.member = :member")
    List<Long> findNewsByMember(@Param("member") Member member);
}





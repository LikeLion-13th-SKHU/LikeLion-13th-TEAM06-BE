package com.dongnering.memberNewsLike;


import com.dongnering.art.domain.Art;
import com.dongnering.member.domain.Member;
import com.dongnering.memberArtLike.MemberArtLike;
import com.dongnering.news.domain.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberNewsLikeRepository extends JpaRepository<MemberNewsLike, Long> {

    boolean existsByMemberAndNews(Member member, News news);

    void deleteByMemberAndNews(Member member, News news);

    @Query("select m.news.newsId from MemberNewsLike m where m.member = :member and m.likeStatus = true")
    List<Long> findNewsByMember(@Param("member") Member member);

    Optional<MemberNewsLike> findByMemberAndNews(Member member, News news);


}





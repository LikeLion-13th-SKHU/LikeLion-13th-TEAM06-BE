package com.dongnering.memberArtLike;


import com.dongnering.art.domain.Art;
import com.dongnering.mypage.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberArtLikeRepository extends JpaRepository<MemberArtLike, Long> {


    @Query("select mal.art.artId from MemberArtLike mal where mal.member = :member")
    List<Long> findArtByMember(@Param("member") Member member);

    boolean existsByMemberAndArt(Member member, Art art);


    void deleteByMemberAndArt(Member member, Art art);
}


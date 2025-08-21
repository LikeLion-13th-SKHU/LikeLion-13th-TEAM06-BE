package com.dongnering.memberArtLike;


import com.dongnering.art.domain.Art;
import com.dongnering.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberArtLikeRepository extends JpaRepository<MemberArtLike, Long> {


    @Query("select mal.art.artId from MemberArtLike mal where mal.member = :member and mal.likeStatus = true")
    List<Long> findArtByMember(@Param("member") Member member);

    boolean existsByMemberAndArt(Member member, Art art);


    void deleteByMemberAndArt(Member member, Art art);

    Optional<MemberArtLike> findByMemberAndArt(Member member, Art art);

}


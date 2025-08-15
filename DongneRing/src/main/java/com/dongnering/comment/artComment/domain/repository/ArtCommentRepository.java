package com.dongnering.comment.artComment.domain.repository;


import com.dongnering.art.domain.Art;
import com.dongnering.comment.artComment.domain.ArtComment;
import com.dongnering.mypage.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtCommentRepository extends JpaRepository<ArtComment, Long> {
    boolean existsByArtAndMember(Art art, Member member);

    ArtComment findByArtAndMember(Art art, Member member);

    List<ArtComment> findAllByArt(Art art);

    Long countByArt(Art art);
}

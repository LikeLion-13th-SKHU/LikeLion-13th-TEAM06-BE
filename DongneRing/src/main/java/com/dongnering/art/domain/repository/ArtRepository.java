package com.dongnering.art.domain.repository;


import com.dongnering.art.domain.Art;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ArtRepository extends JpaRepository<Art, Long> {


    boolean existsByIdentifyId(Long identifyId);

    Page<Art> findAllByLocation(String location, Pageable pageable);

    Page<Art> findAllByOrderByLikeCountDesc(Pageable pageable);


    @Query("select a from Art a where a.artId in :personalCommentList")
    Page<Art> findArtByCommentId(@Param("personalCommentList") List<Long> personalCommentList, Pageable pageable);



}



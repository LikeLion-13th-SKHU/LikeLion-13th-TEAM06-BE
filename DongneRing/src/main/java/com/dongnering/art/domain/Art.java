package com.dongnering.art.domain;


import com.dongnering.memberArtLike.MemberArtLike;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Art {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "art_id")
    private Long artId;

    //공공api에 조회시 db에 중복 저장 방지 id
    private Long identifyId;

    private String title;
    private String startDate;
    private String endDate;

    //장소 - ex)문화회관
    private String area;

    //지역 - ex) 서울, 경기
    private String location;
    private String imageUrl;

    private Long likeCount;

    @OneToMany(mappedBy = "art" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberArtLike> memberArtLikes = new ArrayList<>();


    @Builder
    public Art(Long identifyId, String title, String startDate, String endDate, String area, String location, String imageUrl, Long likeCount) {
        this.identifyId = identifyId;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.area = area;
        this.location = location;
        this.imageUrl = imageUrl;
        this.likeCount = likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }
}

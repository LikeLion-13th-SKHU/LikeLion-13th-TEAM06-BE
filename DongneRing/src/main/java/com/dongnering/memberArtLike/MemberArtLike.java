package com.dongnering.memberArtLike;


import com.dongnering.art.domain.Art;
import com.dongnering.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class MemberArtLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberArtLike")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "art_id")
    private Art art;

    private boolean likeStatus;

    public MemberArtLike(Member member, Art art) {
        this.member = member;
        this.art = art;
        this.likeStatus = false;
    }

    public void setLikeStatus(boolean likeStatus) {
        this.likeStatus = likeStatus;
    }
}

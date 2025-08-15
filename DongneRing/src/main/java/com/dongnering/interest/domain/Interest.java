package com.dongnering.interest.domain;


import com.dongnering.memberInterest.domain.MemberInterest;
import com.dongnering.newsInterest.domain.NewsInterest;
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
public class Interest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long interestId;

    @Enumerated(EnumType.STRING)
    private InterestType interestType;

    @OneToMany(mappedBy = "interest")
    private List<MemberInterest> memberInterests = new ArrayList<>();

    @OneToMany(mappedBy = "interest")
    private List<NewsInterest> newsInterests = new ArrayList<>();



    @Builder
    public Interest(InterestType interestType) {
        this.interestType = interestType;
    }
}

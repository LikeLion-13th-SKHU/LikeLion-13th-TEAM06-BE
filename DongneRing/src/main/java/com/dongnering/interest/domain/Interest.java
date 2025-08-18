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

    // 🔹 추가: getName() 메서드
    public String getName() {
        // InterestType Enum 이름을 한글/카테고리 이름으로 변환하고 싶으면 여기서 매핑 가능
        switch (interestType) {
            case POLICY_GOVERNMENT: return "정책_정부";
            case INDUSTRY_COMPANY: return "산업_기업";
            case RESEARCH_TECHNOLOGY: return "연구_기술";
            case REGULATION_SYSTEM: return "규제_제도";
            case EXPORT_GLOBAL: return "수출_글로벌";
            case INVESTMENT_FINANCE: return "투자_금융";
            case HR_ORGANIZATION: return "인사_조직";
            case SOCIETY: return "사회";
            case OTHERS: return "기타";
            default: return interestType.name();
        }
    }
}

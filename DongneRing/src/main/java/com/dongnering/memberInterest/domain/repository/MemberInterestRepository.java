package com.dongnering.memberInterest.domain.repository;

import com.dongnering.interest.domain.InterestType;
import com.dongnering.member.domain.Member;

import com.dongnering.interest.domain.InterestType;
import com.dongnering.member.domain.Member;
import com.dongnering.memberInterest.domain.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {
    List<MemberInterest> findByMember(Member member);
}

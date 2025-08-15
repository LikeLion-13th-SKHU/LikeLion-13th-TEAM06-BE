package com.dongnering.memberInterest.domain.repository;


import com.dongnering.memberInterest.domain.MemberInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberInterestRepository extends JpaRepository<MemberInterest, Long> {
}

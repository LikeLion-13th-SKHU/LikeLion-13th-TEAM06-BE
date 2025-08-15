package com.dongnering.interest.domain.repository;


import com.dongnering.interest.domain.Interest;
import com.dongnering.interest.domain.InterestType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Boolean existsByInterestType(InterestType interestType);
    Interest findByInterestType(InterestType interestType);
}
